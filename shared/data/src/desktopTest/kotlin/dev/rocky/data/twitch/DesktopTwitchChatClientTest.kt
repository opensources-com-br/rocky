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
    @Test fun duplicateReconnectDoesNotOpenMoreSockets() = withClient { client ->
        client.connect("client", events::add)
        await { sockets.size == 1 }
        welcome(0)
        await { events.any { it is TwitchConnectionEvent.Connected } }
        reconnect()
        reconnect()
        assertEquals(2, sockets.size)
        welcome(1)
    }

    @Test fun transfersOnlyAfterWelcomeWithoutCreatingAnotherSubscription() = withClient { client ->
        client.connect("client", events::add)
        await { sockets.size == 1 }
        welcome(0)
        await { events.any { it is TwitchConnectionEvent.Connected } }
        reconnect()
        assertEquals(2, sockets.size)
        assertEquals(false, sockets[0].first.closed)
        assertEquals("wss://eventsub.wss.twitch.tv/ws?transfer=exact", urls.last())
        welcome(1)
        assertEquals(true, sockets[0].first.closed)
        assertEquals(1, requests.count { it == "POST" })
    }

    private fun send(index: Int, type: String, payload: String) {
        val (socket, listener) = sockets[index]
        listener.onText(socket, """{"metadata":{"message_type":"$type"},"payload":$payload}""", true)
    }
    private fun welcome(index: Int) = send(index, "session_welcome",
        """{"session":{"id":"session-$index","keepalive_timeout_seconds":30}}""")
    private fun reconnect() = send(0, "session_reconnect",
        """{"session":{"reconnect_url":"wss://eventsub.wss.twitch.tv/ws?transfer=exact"}}""")
    private fun await(condition: () -> Boolean) {
        repeat(500) { if (condition()) return; Thread.sleep(10) }
        error("Timed out waiting for Twitch event")
    }

    private val sockets = CopyOnWriteArrayList<Pair<FakeTwitchSocket, WebSocket.Listener>>()
    private val events = CopyOnWriteArrayList<TwitchConnectionEvent>()
    private val requests = CopyOnWriteArrayList<String>()
    private val urls = CopyOnWriteArrayList<String>()
    private fun withClient(test: (DesktopTwitchChatClient) -> Unit) {
        val server = server()
        val base = "http://127.0.0.1:${server.address.port}"
        val client = DesktopTwitchChatClient(HttpClient.newHttpClient(),
            api = TwitchApi(eventsubEndpoint = base, streamsEndpoint = base),
            authenticate = { _, _, _ -> TwitchAuthentication(TwitchTokens("access", "refresh"), TwitchAccount("42", "rocky")) },
            socketConnector = { url, listener ->
                val socket = FakeTwitchSocket()
                urls += url
                sockets += socket to listener
                listener.onOpen(socket)
                CompletableFuture.completedFuture(socket)
            }, welcomeTimeoutSeconds = 1)
        try { test(client) } finally { client.close(); server.stop(0) }
    }
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
