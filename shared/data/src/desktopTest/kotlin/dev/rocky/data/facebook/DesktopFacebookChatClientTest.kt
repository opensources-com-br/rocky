package dev.rocky.data.facebook

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URLDecoder

class DesktopFacebookChatClientTest {
    private fun apiResponse(path: String) = when (path) {
        "/v25.0/oauth/access_token" -> """{"access_token":"user-token"}"""
        "/v25.0/me/accounts" -> """{"data":[{"id":"page","name":"Rocky","access_token":"page-token"}]}"""
        "/v25.0/page/live_videos" -> """{"data":[{"id":"live","title":"Live Rocky"}]}"""
        "/v25.0/live/comments" -> """{"data":[{"id":"comment","message":"Olá do Facebook","created_time":"2026-09-13T18:00:00+0000","from":{"id":"viewer","name":"Ana"}}],"paging":{"cursors":{"after":"next"}}}"""
        "/v25.0/live" -> """{"live_views":42}"""
        else -> error("Unexpected API path: $path")
    }

    private fun HttpExchange.respond(body: String) {
        val bytes = body.toByteArray()
        sendResponseHeaders(200, bytes.size.toLong())
        responseBody.use { it.write(bytes) }
    }

    private fun String.parameters() = split("&").associate {
        val (key, value) = it.split("=", limit = 2)
        URLDecoder.decode(key, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8)
    }

    private fun await(condition: () -> Boolean) {
        repeat(200) {
            if (condition()) return
            Thread.sleep(10)
        }
        error("Timed out waiting for Facebook event")
    }
}
