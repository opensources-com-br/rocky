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

    override fun connect(configuration: FacebookConfiguration, listener: FacebookConnectionListener) = Unit
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
}
