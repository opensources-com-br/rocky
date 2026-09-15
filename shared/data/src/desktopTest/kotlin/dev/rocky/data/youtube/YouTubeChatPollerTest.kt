package dev.rocky.data.youtube

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.youtube.YouTubeBroadcast
import dev.rocky.core.youtube.YouTubeConfiguration
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class YouTubeChatPollerTest {
    @Test fun keepsCursorAndDeduplicatesAfterFailedPoll() = withPoller { poller ->
        body = """{"nextPageToken":"next","items":[{"id":"m1","snippet":{"displayMessage":"Olá"},"authorDetails":{"displayName":"Ana"}}]}"""
        poller.poll()
        status = 503
        assertFailsWith<YouTubeApiException> { poller.poll() }
        status = 200
        poller.poll()
        assertEquals(listOf("m1"), messages)
        assertEquals(2, requests.count { it.contains("pageToken=next") })
    }

    @Test fun respectsLongPollingIntervalsAndContinuesFromNextPage() = withPoller { poller ->
        assertEquals(45_000L, poller.poll())
        assertEquals(45_000L, poller.poll())
        val chatRequests = requests.filter { it.startsWith("/liveChat/messages") }
        assertEquals(false, chatRequests.first().contains("pageToken="))
        assertEquals(true, chatRequests.last().contains("pageToken=next"))
        assertEquals(true, chatRequests.first().contains("fields="))
        assertEquals(true, chatRequests.first().contains("maxResults=500"))
    }

    private var body = """{"items":[],"nextPageToken":"next","pollingIntervalMillis":45000}"""
    private var now = 0L
    private var status = 200
    private val requests = mutableListOf<String>()
    private val messages = mutableListOf<String>()
    private val audience = mutableListOf<Int?>()
    private fun withPoller(test: (YouTubeChatPoller) -> Unit) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            requests += exchange.requestURI.toString()
            val bytes = (if (exchange.requestURI.path == "/videos")
                """{"items":[{"liveStreamingDetails":{"concurrentViewers":"42"}}]}""" else body).toByteArray()
            exchange.sendResponseHeaders(status, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        try {
            val access = YouTubeAccessSession(YouTubeConfiguration(), YouTubeTokenApi(),
                YouTubeTokens("access", "refresh", 3600), { now })
            test(YouTubeChatPoller(YouTubeLiveApi(apiBase = "http://127.0.0.1:${server.address.port}"),
                access, YouTubeBroadcast("video", "chat", "Live"), { messages += it.id }, audience::add, { now }))
        } finally { server.stop(0) }
    }
}
