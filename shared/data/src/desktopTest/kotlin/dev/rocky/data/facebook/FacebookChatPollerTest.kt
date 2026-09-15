package dev.rocky.data.facebook

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.live.ChatMessage
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals

class FacebookChatPollerTest {
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
