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
        var uri = URI(url)
        require(url.startsWith(RELEASE_DOWNLOAD)) { "Origem de atualização inválida." }
        repeat(6) {
            checkCancelled()
            require(uri.scheme == "https" && uri.userInfo == null && uri.port == -1 &&
                uri.host in setOf("github.com", "release-assets.githubusercontent.com", "objects.githubusercontent.com")) {
                "Redirecionamento de atualização inválido."
            }
            val request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(30)).GET().build()
            val future = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
            pending = future
            val response = try { future.get(30, TimeUnit.SECONDS) } finally { future.cancel(true); pending = null }
            stream = response.body()
            response.body().use { input ->
                checkCancelled()
                if (response.statusCode() in setOf(301, 302, 303, 307, 308)) {
                    uri = uri.resolve(response.headers().firstValue("location").orElseThrow())
                } else {
                    check(response.statusCode() == 200) { "Não foi possível baixar a atualização (${response.statusCode()})." }
                    return consume(input).also { checkCancelled() }
                }
            }
        }
        error("Redirecionamentos demais no download da atualização.")
    }
}
