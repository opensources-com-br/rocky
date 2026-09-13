package dev.rocky.data.voice

import java.io.InputStream
import java.net.URI
import java.net.http.*
import java.time.Duration
import java.util.concurrent.*

internal class ElevenLabsSession(private val base: String = "https://api.elevenlabs.io") : AutoCloseable {
    companion object {
        private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
    }
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
    fun open(path: String, key: String, body: String? = null): InputStream {
        require(key.isNotBlank()) { "ElevenLabs: informe sua chave em Voz." }
        checkActive()
        val request = HttpRequest.newBuilder(URI.create(base + path))
            .header("xi-api-key", key.trim()).timeout(Duration.ofSeconds(30))
        if (body == null) request.GET() else request.header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
        val future = client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofInputStream())
        pending = future
        val response = try { checkActive(); future.get(30, TimeUnit.SECONDS) }
            finally { future.cancel(true); pending = null }
        stream = response.body()
        checkActive()
        if (response.statusCode() != 200) {
            response.body().close()
            error(elevenLabsError(response.statusCode()))
        }
        return response.body()
    }
}
