package dev.rocky.data.twitch

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionPhase
import java.net.InetSocketAddress
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DesktopTwitchChatClientTest {
    private val sockets = CopyOnWriteArrayList<Pair<FakeTwitchSocket, WebSocket.Listener>>()
    private val events = CopyOnWriteArrayList<TwitchConnectionEvent>()
    private val requests = CopyOnWriteArrayList<String>()
}
