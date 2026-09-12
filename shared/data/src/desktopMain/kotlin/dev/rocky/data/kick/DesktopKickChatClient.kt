package dev.rocky.data.kick

import dev.rocky.core.kick.KickChatClient
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionListener
import dev.rocky.core.kick.KickConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class DesktopKickChatClient : KickChatClient {
    private val generation = AtomicLong()
    private val ioExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-kick-io").apply { isDaemon = true }
    }
    @Volatile private var active = false
    @Volatile private var listener = KickConnectionListener {}

    override fun connect(configuration: KickConfiguration, listener: KickConnectionListener) {
        require(configuration.clientId.isNotBlank()) { "Informe o Client ID da Kick." }
        require(configuration.clientSecret.isNotBlank()) { "Informe o Client Secret da Kick." }
        generation.incrementAndGet()
        this.listener = listener
        active = true
        emit(KickConnectionPhase.Authenticating, "Preparando autorização da Kick")
    }

    override fun disconnect() {
        active = false
        generation.incrementAndGet()
        emit(KickConnectionPhase.Disconnected)
    }

    override fun close() {
        active = false
        ioExecutor.shutdownNow()
    }

    private fun emit(phase: KickConnectionPhase, detail: String? = null) =
        listener.onEvent(KickConnectionEvent.PhaseChanged(phase, detail))
}
