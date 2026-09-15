package dev.rocky.data.facebook

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.live.ChatMessage
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals

class FacebookChatPollerTest {
    @Test fun refreshesAudienceEveryThirtySecondsAndKeepsLastSuccessfulCount() = withPoller { poller ->
        poller.poll()
        now = 29_999
        poller.poll()
        assertEquals(listOf(42), audience)
        now = 30_000
        audienceStatus = 503
        poller.poll()
        assertEquals(listOf(42), audience)
        now = 60_000
        audienceStatus = 200
        poller.poll()
        assertEquals(listOf(42, 42), audience)
    }

    @Test fun slowsDownIdlePollingAndResetsForNewComments() = withPoller { poller ->
        assertEquals(listOf(3000L, 4000L, 5000L, 5000L), (1..4).map { poller.poll() })
        body = """{"data":[{"id":"1","message":"Olá"}]}"""
        assertEquals(2000L, poller.poll())
        assertEquals(3000L, poller.poll())
        assertEquals(1, messages.size)
    }

    private var body = """{"data":[]}"""
    private var audienceStatus = 200
    private var now = 0L
    private val messages = mutableListOf<ChatMessage>()
    private val audience = mutableListOf<Int?>()
    private fun withPoller(test: (FacebookChatPoller) -> Unit) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val isAudience = exchange.requestURI.path == "/live"
            val bytes = (if (isAudience) """{"live_views":42}""" else body).toByteArray()
            exchange.sendResponseHeaders(if (isAudience) audienceStatus else 200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        try {
            test(FacebookChatPoller(FacebookGraphApi(apiBase = "http://127.0.0.1:${server.address.port}"),
                "token", FacebookLiveVideo("live", "Live"), messages::add, audience::add, { now }))
        } finally { server.stop(0) }
    }
}
