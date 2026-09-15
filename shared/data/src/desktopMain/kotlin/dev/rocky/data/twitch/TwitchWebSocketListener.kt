package dev.rocky.data.twitch

import java.net.http.WebSocket
import java.util.concurrent.CompletionStage

internal class TwitchWebSocketListener(
    private val onOpened: (WebSocket) -> Unit,
    private val onEvent: (WebSocket, TwitchSocketEvent) -> Unit,
    private val onClosed: (WebSocket, Throwable?) -> Unit,
    private val maxMessageChars: Int = 1_048_576,
) : WebSocket.Listener {
    private val message = StringBuilder()

    override fun onOpen(webSocket: WebSocket) {
        onOpened(webSocket)
        webSocket.request(1)
    }

    override fun onText(
        webSocket: WebSocket,
        data: CharSequence,
        last: Boolean,
    ): CompletionStage<*>? {
        if (data.length > maxMessageChars - message.length) {
            message.clear()
            webSocket.abort()
            onClosed(webSocket, IllegalArgumentException("Twitch message exceeded the size limit"))
            return null
        }
        message.append(data)
        if (last) {
            val body = message.toString()
            message.clear()
            runCatching { TwitchSocketPayloads.parse(body) }
                .onSuccess { onEvent(webSocket, it) }
        }
        webSocket.request(1)
        return null
    }

    override fun onPing(webSocket: WebSocket, message: java.nio.ByteBuffer): CompletionStage<*>? {
        webSocket.request(1)
        return webSocket.sendPong(message)
    }

    override fun onClose(
        webSocket: WebSocket,
        statusCode: Int,
        reason: String,
    ): CompletionStage<*>? {
        onClosed(webSocket, null)
        return null
    }

    override fun onError(webSocket: WebSocket, error: Throwable) {
        onClosed(webSocket, error)
    }
}
