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
        this.configuration = configuration.copy(
            clientId = configuration.clientId.trim(),
            clientSecret = configuration.clientSecret.trim(),
            redirectUri = configuration.redirectUri.trim(),
        )
        this.listener = listener
        active = true
        emit(YouTubeConnectionPhase.Authenticating, "Preparando autorização do YouTube")
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
}
