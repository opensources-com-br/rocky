package dev.rocky.data.twitch

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionPhase
import java.net.InetSocketAddress
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DesktopTwitchChatClientTest {
    private val sockets = CopyOnWriteArrayList<Pair<FakeTwitchSocket, WebSocket.Listener>>()
    private val events = CopyOnWriteArrayList<TwitchConnectionEvent>()
    private val requests = CopyOnWriteArrayList<String>()
    private val urls = CopyOnWriteArrayList<String>()
    private fun server(): HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
        createContext("/") { exchange ->
            requests += exchange.requestMethod
            val bytes = """{"data":[]}""".toByteArray()
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        start()
    }
}
