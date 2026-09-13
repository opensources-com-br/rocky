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
    @Test fun sanitizesRemoteErrorsAndRejectsRedirects() {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val code = exchange.requestURI.path.drop(1).toInt()
            exchange.responseHeaders.set("Location", "http://127.0.0.1:1/leak")
            exchange.sendResponseHeaders(code, 0)
            exchange.responseBody.use { it.write("private provider detail".toByteArray()) }
        }
        server.start()
        try {
            for (code in listOf(401, 403, 429, 500, 302)) {
                ElevenLabsSession("http://127.0.0.1:${server.address.port}").use { session ->
                    val error = assertFailsWith<IllegalStateException> { session.open("/$code", "secret") }
                    assertEquals(elevenLabsError(code), error.message)
                    assertFalse(error.message!!.contains("secret"))
                }
            }
        } finally { server.stop(0) }
    }
}
