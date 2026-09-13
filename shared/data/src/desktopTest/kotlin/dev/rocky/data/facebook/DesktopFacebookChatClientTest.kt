package dev.rocky.data.facebook

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionEvent
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.URI
import java.net.URLDecoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesktopFacebookChatClientTest {
    @Test fun connectsToTheActivePageAndReceivesComments() {
        val callbackPort = ServerSocket(0).use { it.localPort }
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            createContext("/") { exchange -> exchange.respond(apiResponse(exchange.requestURI.path)) }
            start()
        }
        val events = CopyOnWriteArrayList<FacebookConnectionEvent>()
        val client = DesktopFacebookChatClient(
            FacebookGraphApi(apiBase = "http://127.0.0.1:${server.address.port}/v25.0"),
        )
        try {
            client.connect(
                FacebookConfiguration("app", "secret", "http://127.0.0.1:$callbackPort/oauth/facebook/callback"),
                events::add,
            )
            await { events.filterIsInstance<FacebookConnectionEvent.AuthorizationRequired>().isNotEmpty() }
            val authorization = events.filterIsInstance<FacebookConnectionEvent.AuthorizationRequired>().single()
            val state = URI.create(authorization.authorizationUri).rawQuery.parameters().getValue("state")
            val callback = HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:$callbackPort/oauth/facebook/callback?code=code&state=$state"),
            ).GET().build()
            assertEquals(200, HttpClient.newHttpClient().send(callback, HttpResponse.BodyHandlers.ofString()).statusCode())

            await { events.any { it is FacebookConnectionEvent.MessageReceived } }
            assertTrue(events.any { it is FacebookConnectionEvent.Connected && it.page.name == "Rocky" })
            assertTrue(events.any { it is FacebookConnectionEvent.AudienceUpdated && it.viewerCount == 42 })
            assertEquals("Olá do Facebook", events.filterIsInstance<FacebookConnectionEvent.MessageReceived>().single().message.text)
        } finally {
            client.close()
            server.stop(0)
        }
    }

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
