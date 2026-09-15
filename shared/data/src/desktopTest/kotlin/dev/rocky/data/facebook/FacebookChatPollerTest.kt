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
}
