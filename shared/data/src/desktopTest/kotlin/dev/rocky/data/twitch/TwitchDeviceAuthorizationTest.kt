package dev.rocky.data.twitch

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.twitch.TwitchConnectionEvent
import java.net.InetSocketAddress
import java.net.http.HttpClient
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class TwitchDeviceAuthorizationTest {
    @Test fun productionFlowEmitsCodeAndBrowserLink() = authorize()
    private fun authorize() {
        val server = server()
        val events = CopyOnWriteArrayList<TwitchConnectionEvent>()
        val client = DesktopTwitchChatClient(HttpClient.newHttpClient(),
            api = TwitchApi(deviceEndpoint = "http://127.0.0.1:${server.address.port}"))
        try {
            client.connect("client", events::add)
            await { events.any { it is TwitchConnectionEvent.AuthorizationRequired } }
            val authorization = events.filterIsInstance<TwitchConnectionEvent.AuthorizationRequired>().single()
            assertEquals("CODE", authorization.userCode)
            assertEquals("https://www.twitch.tv/activate", authorization.verificationUri)
            assertEquals(if (firstFailure) 2 else 1, calls.get())
        } finally { client.close(); server.stop(0) }
    }

    private fun server() = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
        createContext("/") { exchange ->
            val status = if (calls.incrementAndGet() == 1 && firstFailure) 503 else 200
            val body = """{"device_code":"device","user_code":"CODE","verification_uri":"https://www.twitch.tv/activate","interval":60,"expires_in":300}"""
                .toByteArray()
            exchange.sendResponseHeaders(status, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        start()
    }
    private fun await(condition: () -> Boolean) {
        repeat(600) { if (condition()) return; Thread.sleep(10) }
        error("Timed out waiting for Twitch authorization")
    }

    private val calls = AtomicInteger()
    private var firstFailure = false
}
