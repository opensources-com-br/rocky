package dev.rocky.data.updates

import java.io.InputStream
import java.net.URI
import java.net.http.*
import java.time.Duration
import java.util.concurrent.*

internal class UpdateTransfer {
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build()
    @Volatile private var pending: CompletableFuture<*>? = null
    @Volatile private var stream: InputStream? = null
    @Volatile private var cancelled = false

    fun cancel() {
        cancelled = true
        pending?.cancel(true)
        runCatching { stream?.close() }
    }

    fun checkCancelled() {
        if (cancelled || Thread.currentThread().isInterrupted) throw InterruptedException("Download cancelado")
    }

    fun <T> read(url: String, consume: (InputStream) -> T): T {
        require(url.startsWith(RELEASE_DOWNLOAD)) { "Origem de atualização inválida." }
        checkCancelled()
        val request = HttpRequest.newBuilder(URI(url)).timeout(Duration.ofSeconds(30)).GET().build()
        val future = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
        pending = future
        val response = try { future.get(30, TimeUnit.SECONDS) } finally { future.cancel(true); pending = null }
        stream = response.body()
        return response.body().use { input ->
            checkCancelled()
            check(response.statusCode() == 200) { "Não foi possível baixar a atualização." }
            consume(input).also { checkCancelled() }
        }
    }
}
