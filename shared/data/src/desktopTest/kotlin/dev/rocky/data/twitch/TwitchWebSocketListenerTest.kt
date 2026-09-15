package dev.rocky.data.twitch

import kotlin.test.Test
import kotlin.test.assertEquals

class TwitchWebSocketListenerTest {
    @Test fun parsesFragmentedKeepaliveOnceAndRequestsMoreFrames() {
        val events = mutableListOf<TwitchSocketEvent>()
        val socket = FakeTwitchSocket()
        val listener = TwitchWebSocketListener({}, { _, event -> events += event }, { _, _ -> })
        listener.onOpen(socket)
        listener.onText(socket, """{"metadata":{"message_type":""", false)
        assertEquals(0, events.size)
        listener.onText(socket, """"session_keepalive"},"payload":{}}""", true)
        assertEquals(listOf<TwitchSocketEvent>(TwitchSocketEvent.Keepalive), events)
        assertEquals(3L, socket.requested)
    }
}
