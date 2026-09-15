package dev.rocky.data.youtube

import dev.rocky.core.youtube.YouTubeChatClient
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionEvent
import dev.rocky.core.youtube.YouTubeConnectionListener
import dev.rocky.core.youtube.YouTubeConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DesktopYouTubeChatClient internal constructor(
    private val tokenApi: YouTubeTokenApi,
    private val liveApi: YouTubeLiveApi,
    private val currentTimeMillis: () -> Long,
) : YouTubeChatClient {
    constructor() : this(YouTubeTokenApi(), YouTubeLiveApi(), System::currentTimeMillis)

    private val generation = AtomicLong()
    private val ioExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-youtube-io").apply { isDaemon = true }
    }
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { task ->
        Thread(task, "rocky-youtube-poll").apply { isDaemon = true }
    }
    @Volatile private var active = false
    @Volatile private var listener = YouTubeConnectionListener {}
    @Volatile private var configuration = YouTubeConfiguration()
    @Volatile private var receiver: YouTubeAuthorizationReceiver? = null
    @Volatile private var poller: YouTubeChatPoller? = null
    private var pendingPoll: ScheduledFuture<*>? = null
    private var pendingRequest: Future<*>? = null
    private var retryAttempt = 0
    @Volatile private var closed = false

    @Synchronized
    override fun connect(configuration: YouTubeConfiguration, listener: YouTubeConnectionListener) {
        check(!closed) { "O cliente do YouTube já foi encerrado." }
        stop(notify = false)
        require(configuration.clientId.isNotBlank()) { "Informe o Client ID do YouTube." }
        require(configuration.clientSecret.isNotBlank()) { "Informe o Client Secret do YouTube." }
        val run = generation.incrementAndGet()
        this.configuration = configuration.copy(
            clientId = configuration.clientId.trim(),
            clientSecret = configuration.clientSecret.trim(),
            redirectUri = configuration.redirectUri.trim(),
        )
        this.listener = listener
        active = true
        emit(YouTubeConnectionPhase.Authenticating, "Preparando autorização do YouTube")
        pendingRequest = ioExecutor.submit { if (isCurrent(run)) prepareAuthorization(run) }
    }

    @Synchronized
    private fun prepareAuthorization(run: Long) {
        if (!isCurrent(run)) return
        runCatching {
            val authorization = createYouTubeAuthorization(configuration.clientId, configuration.redirectUri)
            receiver = YouTubeAuthorizationReceiver(configuration.redirectUri, authorization.state) { code ->
                completeAuthorization(run, authorization.verifier, code)
            }
            authorization.uri
        }.onSuccess { uri ->
            if (isCurrent(run)) {
                emit(YouTubeConnectionPhase.AwaitingAuthorization, "Autorize o canal no navegador")
                emitForRun(run, YouTubeConnectionEvent.AuthorizationRequired(uri))
            }
        }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
    }

    @Synchronized
    private fun completeAuthorization(run: Long, verifier: String, code: String) {
        if (!isCurrent(run)) return
        receiver?.close()
        receiver = null
        emit(YouTubeConnectionPhase.FindingBroadcast, "Procurando uma live ativa no canal")
        val configuration = this.configuration
        pendingRequest = ioExecutor.submit {
            if (!isCurrent(run)) return@submit
            runCatching {
                val tokens = tokenApi.exchangeCode(
                    configuration.clientId,
                    configuration.clientSecret,
                    configuration.redirectUri,
                    verifier,
                    code,
                )
                val access = YouTubeAccessSession(configuration, tokenApi, tokens, currentTimeMillis)
                val account = access.request(liveApi::account)
                val broadcast = requireNotNull(access.request(liveApi::activeBroadcast)) {
                    "Nenhuma live ativa com chat foi encontrada no canal do YouTube."
                }
                Triple(access, account, broadcast)
            }.onSuccess { (access, account, broadcast) -> synchronized(this) {
                if (isCurrent(run)) {
                    poller = YouTubeChatPoller(
                        liveApi,
                        access,
                        broadcast,
                        onMessage = { emitForRun(run, YouTubeConnectionEvent.MessageReceived(it)) },
                        onAudience = { emitForRun(run, YouTubeConnectionEvent.AudienceUpdated(it)) },
                        currentTimeMillis = currentTimeMillis,
                    )
                    listener.onEvent(YouTubeConnectionEvent.Connected(account, broadcast))
                    schedulePoll(run, 0)
                }
            } }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    @Synchronized
    private fun schedulePoll(run: Long, delayMillis: Long) {
        if (!isCurrent(run) || scheduler.isShutdown) return
        pendingPoll?.cancel(false)
        pendingPoll = scheduler.schedule({ synchronized(this) {
            if (isCurrent(run) && !ioExecutor.isShutdown) pendingRequest = ioExecutor.submit { poll(run) }
        } }, delayMillis, TimeUnit.MILLISECONDS)
    }

    private fun poll(run: Long) {
        val currentPoller = synchronized(this) {
            if (!isCurrent(run)) return
            requireNotNull(poller)
        }
        runCatching { currentPoller.poll() }
            .onSuccess { synchronized(this) {
                if (isCurrent(run)) { retryAttempt = 0; schedulePoll(run, it) }
            } }
            .onFailure { error -> synchronized(this) {
                if (isCurrent(run)) {
                    val delay = youtubeRetryDelay(error, ++retryAttempt)
                    if (delay == null) fail(run, error.userMessage())
                    else schedulePoll(run, maxOf(delay, currentPoller.minimumPollDelayMillis))
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
        pendingRequest?.cancel(true)
        pendingRequest = null
        retryAttempt = 0
        receiver?.close()
        receiver = null
        poller = null
        if (notify) emit(YouTubeConnectionPhase.Disconnected)
    }

    private fun emit(phase: YouTubeConnectionPhase, detail: String? = null) =
        listener.onEvent(YouTubeConnectionEvent.PhaseChanged(phase, detail))

    @Synchronized
    private fun emitForRun(run: Long, event: YouTubeConnectionEvent) {
        if (isCurrent(run)) listener.onEvent(event)
    }

    @Synchronized
    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        active = false
        pendingPoll?.cancel(false)
        pendingPoll = null
        receiver?.close()
        receiver = null
        poller = null
        emit(YouTubeConnectionPhase.Failed, message)
    }

    private fun isCurrent(run: Long) = !closed && active && generation.get() == run
    private fun Throwable.userMessage() = message?.takeIf { it.isNotBlank() }
        ?: "Não foi possível conectar com o YouTube."
}
