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
