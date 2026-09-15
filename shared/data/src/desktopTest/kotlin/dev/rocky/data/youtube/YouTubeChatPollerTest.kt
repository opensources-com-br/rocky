package dev.rocky.data.youtube

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.youtube.YouTubeBroadcast
import dev.rocky.core.youtube.YouTubeConfiguration
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class YouTubeChatPollerTest {
    private var body = """{"items":[],"nextPageToken":"next","pollingIntervalMillis":45000}"""
    private var now = 0L
    private var status = 200
    private val requests = mutableListOf<String>()
    private val messages = mutableListOf<String>()
    private val audience = mutableListOf<Int?>()
}
