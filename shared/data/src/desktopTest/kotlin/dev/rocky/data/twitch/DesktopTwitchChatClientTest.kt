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
    @Test fun closeTwiceRejectsFurtherConnections() {
        val client = DesktopTwitchChatClient()
        client.close()
        client.close()
        assertFailsWith<IllegalStateException> { client.connect("client", {}) }
    }

    @Test fun missingWelcomeSchedulesRetryWhichDisconnectCancels() = withClient { client ->
        client.connect("client", events::add)
        await { sockets.size == 1 }
        await { events.any { it is TwitchConnectionEvent.PhaseChanged && it.phase == TwitchConnectionPhase.Reconnecting } }
        assertEquals(true, sockets[0].first.aborted)
        client.disconnect()
        Thread.sleep(2_200)
        assertEquals(1, sockets.size)
    }

    @Test fun disconnectAbortsSocketAndRejectsLateOpen() = withClient { client ->
        client.connect("client", events::add)
        await { sockets.size == 1 }
        client.disconnect()
        val lateSocket = FakeTwitchSocket()
        sockets[0].second.onOpen(lateSocket)
        welcome(0)
        assertEquals(true, sockets[0].first.aborted)
        assertEquals(true, lateSocket.aborted)
        assertEquals(0, requests.size)
    }

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
    private fun message(index: Int, id: String) = send(index, "notification",
        """{"event":{"message_id":"$id","chatter_user_name":"Ana","message":{"text":"Olá"}}}""")
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
