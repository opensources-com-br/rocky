package dev.rocky.data.facebook

import dev.rocky.core.facebook.FacebookChatClient
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionEvent
import dev.rocky.core.facebook.FacebookConnectionListener
import dev.rocky.core.facebook.FacebookConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
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

    override fun connect(configuration: FacebookConfiguration, listener: FacebookConnectionListener) {
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
        ioExecutor.execute { prepareAuthorization(run) }
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

    private fun completeAuthorization(run: Long, code: String) = Unit
    override fun disconnect() = stop(notify = true)
    override fun close() {
        stop(notify = false)
        scheduler.shutdownNow()
        ioExecutor.shutdownNow()
        ioExecutor.awaitTermination(3, TimeUnit.SECONDS)
    }

    private fun stop(notify: Boolean) {
        active = false
        generation.incrementAndGet()
        receiver?.close()
        receiver = null
        poller = null
        if (notify) emit(FacebookConnectionPhase.Disconnected)
    }

    private fun emit(phase: FacebookConnectionPhase, detail: String? = null) =
        listener.onEvent(FacebookConnectionEvent.PhaseChanged(phase, detail))

    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        active = false
        receiver?.close()
        receiver = null
        poller = null
        emit(FacebookConnectionPhase.Failed, message)
    }

    private fun isCurrent(run: Long) = active && generation.get() == run
    private fun Throwable.userMessage() = message?.takeIf { it.isNotBlank() }
        ?: "Não foi possível conectar com o Facebook."
}
