package dev.rocky.data.voice

import java.io.InputStream
import java.net.URI
import java.net.http.*
import java.time.Duration
import java.util.concurrent.*

internal class ElevenLabsSession(private val base: String = "https://api.elevenlabs.io") : AutoCloseable {
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
    @Volatile private var pending: CompletableFuture<*>? = null
    @Volatile private var stream: InputStream? = null
    @Volatile private var cancelled = false
    private val timer = Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "rocky-speech-timeout").apply { isDaemon = true } }
    init { timer.schedule({ close() }, 120, TimeUnit.SECONDS) }
    fun checkActive() {
        if (cancelled || Thread.currentThread().isInterrupted) throw InterruptedException("Áudio cancelado")
    }
    override fun close() {
        cancelled = true
        pending?.cancel(true)
        runCatching { stream?.close() }
        timer.shutdownNow()
    }
}
