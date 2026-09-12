package dev.rocky.data.youtube

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionEvent
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

class DesktopYouTubeChatClientTest {
    @Test fun connectsToTheActiveBroadcastAndReceivesChat() {
        val callbackPort = ServerSocket(0).use { it.localPort }
        val api = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            createContext("/") { exchange -> exchange.respond(apiResponse(exchange.requestURI.path)) }
            start()
        }
        val base = "http://127.0.0.1:${api.address.port}"
        val events = CopyOnWriteArrayList<YouTubeConnectionEvent>()
        val client = DesktopYouTubeChatClient(
            YouTubeTokenApi(tokenEndpoint = "$base/token"),
            YouTubeLiveApi(apiBase = base),
            System::currentTimeMillis,
        )

        try {
            client.connect(
                YouTubeConfiguration("client", "secret", "http://127.0.0.1:$callbackPort/oauth/youtube/callback"),
                events::add,
            )
            await { events.filterIsInstance<YouTubeConnectionEvent.AuthorizationRequired>().firstOrNull() != null }
            val authorization = events.filterIsInstance<YouTubeConnectionEvent.AuthorizationRequired>().single()
            val state = URI.create(authorization.authorizationUri).rawQuery.parameters().getValue("state")
            val callback = HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:$callbackPort/oauth/youtube/callback?code=code&state=$state"),
            ).GET().build()
            assertEquals(200, HttpClient.newHttpClient().send(callback, HttpResponse.BodyHandlers.ofString()).statusCode())

            await { events.any { it is YouTubeConnectionEvent.MessageReceived } }
            assertTrue(events.any { it is YouTubeConnectionEvent.Connected && it.account.displayName == "Rocky" })
            assertTrue(events.any { it is YouTubeConnectionEvent.AudienceUpdated && it.viewerCount == 42 })
            assertEquals("Olá do YouTube", events.filterIsInstance<YouTubeConnectionEvent.MessageReceived>().single().message.text)
        } finally {
            client.close()
            api.stop(0)
        }
    }

    private fun apiResponse(path: String) = when (path) {
        "/token" -> """{"access_token":"access","refresh_token":"refresh","expires_in":3600}"""
        "/channels" -> """{"items":[{"id":"channel","snippet":{"title":"Rocky"}}]}"""
        "/liveBroadcasts" -> """{"items":[{"id":"video","snippet":{"title":"Live","liveChatId":"chat"}}]}"""
        "/liveChat/messages" -> """{"nextPageToken":"next","pollingIntervalMillis":30000,"items":[{"id":"message","snippet":{"displayMessage":"Olá do YouTube","publishedAt":"2026-09-12T18:00:00Z"},"authorDetails":{"channelId":"viewer","displayName":"Ana"}}]}"""
        "/videos" -> """{"items":[{"liveStreamingDetails":{"concurrentViewers":"42"}}]}"""
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
        error("Timed out waiting for YouTube event")
    }
}
