package dev.rocky.data.voice

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlin.test.*

class ElevenLabsSessionTest {
    @Test fun sendsAuthenticationAndStreamsResponse() {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        var receivedKey: String? = null
        server.createContext("/speech") { exchange ->
            receivedKey = exchange.requestHeaders.getFirst("xi-api-key")
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.use { it.write(byteArrayOf(1, 2, 3, 4)) }
        }
        server.start()
        try {
            ElevenLabsSession("http://127.0.0.1:${server.address.port}").use { session ->
                assertContentEquals(byteArrayOf(1, 2, 3, 4), session.open("/speech", "test-key", "{}").readBytes())
                assertEquals("test-key", receivedKey)
            }
        } finally { server.stop(0) }
    }
}
