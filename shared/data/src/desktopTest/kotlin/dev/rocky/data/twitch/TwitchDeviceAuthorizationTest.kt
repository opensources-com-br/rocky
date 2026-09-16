package dev.rocky.data.twitch

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.twitch.TwitchConnectionEvent
import java.net.InetSocketAddress
import java.net.http.HttpClient
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class TwitchDeviceAuthorizationTest {
    private val calls = AtomicInteger()
    private var firstFailure = false
}
