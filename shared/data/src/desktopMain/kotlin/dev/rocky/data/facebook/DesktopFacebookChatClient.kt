package dev.rocky.data.facebook

import dev.rocky.core.facebook.FacebookChatClient
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionEvent
import dev.rocky.core.facebook.FacebookConnectionListener
import dev.rocky.core.facebook.FacebookConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DesktopFacebookChatClient internal constructor(
    private val api: FacebookGraphApi,
) : FacebookChatClient {
    constructor() : this(FacebookGraphApi())

    private val generation = AtomicLong()
    private val ioExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-facebook-io").apply { isDaemon = true }
    }
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { task ->
        Thread(task, "rocky-facebook-poll").apply { isDaemon = true }
    }
    @Volatile private var active = false
    @Volatile private var listener = FacebookConnectionListener {}
    @Volatile private var configuration = FacebookConfiguration()
    @Volatile private var receiver: FacebookAuthorizationReceiver? = null
    @Volatile private var poller: FacebookChatPoller? = null
    private var pendingPoll: ScheduledFuture<*>? = null
    private var retryAttempt = 0
    @Volatile private var closed = false

    @Synchronized
    override fun connect(configuration: FacebookConfiguration, listener: FacebookConnectionListener) {
        check(!closed) { "O cliente do Facebook já foi encerrado." }
        stop(notify = false)
        require(configuration.appId.isNotBlank()) { "Informe o App ID do Facebook." }
        require(configuration.appSecret.isNotBlank()) { "Informe o App Secret do Facebook." }
        val run = generation.incrementAndGet()
        this.configuration = configuration.copy(
            appId = configuration.appId.trim(),
            appSecret = configuration.appSecret.trim(),
            redirectUri = configuration.redirectUri.trim(),
        )
        this.listener = listener
        active = true
        emit(FacebookConnectionPhase.Authenticating, "Preparando autorização do Facebook")
        ioExecutor.execute { if (isCurrent(run)) prepareAuthorization(run) }
    }

    private fun prepareAuthorization(run: Long) {
        runCatching {
            val authorization = createFacebookAuthorization(configuration.appId, configuration.redirectUri)
            receiver = FacebookAuthorizationReceiver(configuration.redirectUri, authorization.state) { code ->
                completeAuthorization(run, code)
            }
            authorization.uri
        }.onSuccess { uri ->
            if (isCurrent(run)) {
                emit(FacebookConnectionPhase.AwaitingAuthorization, "Autorize a Página no navegador")
                listener.onEvent(FacebookConnectionEvent.AuthorizationRequired(uri))
            }
        }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
    }

    private fun completeAuthorization(run: Long, code: String) {
        if (!isCurrent(run)) return
        receiver?.close()
        receiver = null
        emit(FacebookConnectionPhase.FindingLive, "Procurando uma live ativa nas suas Páginas")
        ioExecutor.execute {
            runCatching {
                val userToken = api.exchangeCode(
                    configuration.appId,
                    configuration.appSecret,
                    configuration.redirectUri,
                    code,
                )
                api.pages(userToken).firstNotNullOfOrNull { access ->
                    api.activeLiveVideo(access.page.id, access.accessToken)?.let { live -> access to live }
                } ?: error("Nenhuma live ativa foi encontrada nas Páginas autorizadas do Facebook.")
            }.onSuccess { (access, live) ->
                if (isCurrent(run)) {
                    poller = FacebookChatPoller(
                        api,
                        access.accessToken,
                        live,
                        onMessage = { if (isCurrent(run)) listener.onEvent(FacebookConnectionEvent.MessageReceived(it)) },
                        onAudience = { if (isCurrent(run)) listener.onEvent(FacebookConnectionEvent.AudienceUpdated(it)) },
                    )
                    listener.onEvent(FacebookConnectionEvent.Connected(access.page, live))
                    schedulePoll(run, 0)
                }
            }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    @Synchronized
    private fun schedulePoll(run: Long, delayMillis: Long) {
        if (!isCurrent(run) || scheduler.isShutdown) return
        pendingPoll?.cancel(false)
        pendingPoll = scheduler.schedule({
            if (isCurrent(run) && !ioExecutor.isShutdown) ioExecutor.execute { poll(run) }
        }, delayMillis, TimeUnit.MILLISECONDS)
    }

    private fun poll(run: Long) {
        if (!isCurrent(run)) return
        runCatching { requireNotNull(poller).poll() }
            .onSuccess { synchronized(this) {
                if (isCurrent(run)) { retryAttempt = 0; schedulePoll(run, it) }
            } }
            .onFailure { error -> synchronized(this) {
                if (isCurrent(run)) {
                    val delay = facebookRetryDelay(error, ++retryAttempt)
                    if (delay == null) fail(run, error.userMessage()) else schedulePoll(run, delay)
                }
            } }
    }
    override fun disconnect() = stop(notify = true)
    @Synchronized
    override fun close() {
        if (closed) return
        closed = true
        stop(notify = false)
        scheduler.shutdownNow()
        ioExecutor.shutdownNow()
    }

    @Synchronized
    private fun stop(notify: Boolean) {
        active = false
        generation.incrementAndGet()
        pendingPoll?.cancel(false)
        pendingPoll = null
        retryAttempt = 0
        receiver?.close()
        receiver = null
        poller = null
        if (notify) emit(FacebookConnectionPhase.Disconnected)
    }

    private fun emit(phase: FacebookConnectionPhase, detail: String? = null) =
        listener.onEvent(FacebookConnectionEvent.PhaseChanged(phase, detail))

    @Synchronized
    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        active = false
        pendingPoll?.cancel(false)
        pendingPoll = null
        receiver?.close()
        receiver = null
        poller = null
        emit(FacebookConnectionPhase.Failed, message)
    }

    private fun isCurrent(run: Long) = !closed && active && generation.get() == run
    private fun Throwable.userMessage() = message?.takeIf { it.isNotBlank() }
        ?: "Não foi possível conectar com o Facebook."
}
