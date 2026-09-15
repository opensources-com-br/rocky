package dev.rocky.data.twitch

import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

internal class FakeTwitchSocket : WebSocket {
    var aborted = false
    var closed = false
    var requested = 0L
    override fun sendText(data: CharSequence, last: Boolean) = CompletableFuture.completedFuture<WebSocket>(this)
    override fun sendBinary(data: ByteBuffer, last: Boolean) = CompletableFuture.completedFuture<WebSocket>(this)
    override fun sendPing(message: ByteBuffer) = CompletableFuture.completedFuture<WebSocket>(this)
    override fun sendPong(message: ByteBuffer) = CompletableFuture.completedFuture<WebSocket>(this)
}
