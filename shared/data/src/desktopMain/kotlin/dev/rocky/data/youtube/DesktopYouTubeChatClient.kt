package dev.rocky.data.youtube

import dev.rocky.core.youtube.YouTubeChatClient
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionEvent
import dev.rocky.core.youtube.YouTubeConnectionListener
import dev.rocky.core.youtube.YouTubeConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
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

    override fun connect(configuration: YouTubeConfiguration, listener: YouTubeConnectionListener) {
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
        ioExecutor.execute { prepareAuthorization(run) }
    }

    private fun prepareAuthorization(run: Long) {
        runCatching {
            val authorization = createYouTubeAuthorization(configuration.clientId, configuration.redirectUri)
            receiver = YouTubeAuthorizationReceiver(configuration.redirectUri, authorization.state) { code ->
                completeAuthorization(run, authorization.verifier, code)
            }
            authorization.uri
        }.onSuccess { uri ->
            if (isCurrent(run)) {
                emit(YouTubeConnectionPhase.AwaitingAuthorization, "Autorize o canal no navegador")
                listener.onEvent(YouTubeConnectionEvent.AuthorizationRequired(uri))
            }
        }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
    }

    private fun completeAuthorization(run: Long, verifier: String, code: String) {
        if (!isCurrent(run)) return
        receiver?.close()
        receiver = null
        emit(YouTubeConnectionPhase.FindingBroadcast, "Procurando uma live ativa no canal")
        ioExecutor.execute {
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
            }.onSuccess { (access, account, broadcast) ->
                if (isCurrent(run)) {
                    poller = YouTubeChatPoller(
                        liveApi,
                        access,
                        broadcast,
                        onMessage = { if (isCurrent(run)) listener.onEvent(YouTubeConnectionEvent.MessageReceived(it)) },
                        onAudience = { if (isCurrent(run)) listener.onEvent(YouTubeConnectionEvent.AudienceUpdated(it)) },
                        currentTimeMillis = currentTimeMillis,
                    )
                    listener.onEvent(YouTubeConnectionEvent.Connected(account, broadcast))
                    schedulePoll(run, 0)
                }
            }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    private fun schedulePoll(run: Long, delayMillis: Long) {
        if (!isCurrent(run) || scheduler.isShutdown) return
        scheduler.schedule({
            if (isCurrent(run) && !ioExecutor.isShutdown) ioExecutor.execute { poll(run) }
        }, delayMillis, TimeUnit.MILLISECONDS)
    }

    private fun poll(run: Long) {
        runCatching { requireNotNull(poller).poll() }
            .onSuccess { if (isCurrent(run)) schedulePoll(run, it) }
            .onFailure { if (isCurrent(run)) fail(run, it.userMessage()) }
    }

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
        if (notify) emit(YouTubeConnectionPhase.Disconnected)
    }

    private fun emit(phase: YouTubeConnectionPhase, detail: String? = null) =
        listener.onEvent(YouTubeConnectionEvent.PhaseChanged(phase, detail))

    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        active = false
        receiver?.close()
        receiver = null
        poller = null
        emit(YouTubeConnectionPhase.Failed, message)
    }

    private fun isCurrent(run: Long) = active && generation.get() == run
    private fun Throwable.userMessage() = message?.takeIf { it.isNotBlank() }
        ?: "Não foi possível conectar com o YouTube."
}
