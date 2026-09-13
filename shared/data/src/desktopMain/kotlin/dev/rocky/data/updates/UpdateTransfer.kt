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

}
