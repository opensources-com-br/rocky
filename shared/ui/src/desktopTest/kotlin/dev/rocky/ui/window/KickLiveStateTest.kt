package dev.rocky.ui.window

import dev.rocky.core.kick.*
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import org.junit.Assert.assertEquals
import org.junit.Test

class KickLiveStateTest {
    @Test fun receivesKickMessagesWithSessionMetadata() {
        var now = 1_000L
        val client = FakeKickClient()
        val state = KickLiveState(client) { now }
        state.connect(KickConfiguration("id", "secret"))
        client.emit(KickConnectionEvent.Connected(KickAccount("42", "channel")))
        now = 2_000L
        client.emit(KickConnectionEvent.MessageReceived(
            ChatMessage("m1", "viewer", "Olá", StreamPlatform.Kick, channelId = "42")))
        assertEquals(StreamPlatform.Kick, state.messages.single().platform)
        assertEquals("kick-42-1000", state.messages.single().sessionId)
        assertEquals(listOf("m1"), state.messagesReceivedWithin(500).map { it.id })
        assertEquals(1, state.messagesPerMinute)
        assertEquals(1, state.pulse.single().messagesPerMinute)
        now = 63_000L
        state.refreshMetrics()
        assertEquals(0, state.messagesPerMinute)
        assertEquals(0, state.pulse.last().messagesPerMinute)
    }

    @Test fun clearsMessagesBeforeAnewKickSession() {
        val client = FakeKickClient()
        val state = KickLiveState(client) { 1_000L }
        val configuration = KickConfiguration("id", "secret")
        state.connect(configuration)
        client.emit(KickConnectionEvent.Connected(KickAccount("42", "channel")))
        client.emit(KickConnectionEvent.MessageReceived(
            ChatMessage("old", "viewer", "Antiga", StreamPlatform.Kick)))

        state.disconnect()
        state.connect(configuration)

        assertEquals(emptyList<ChatMessage>(), state.messages)
        assertEquals(0, state.totalMessages)
    }

    private class FakeKickClient : KickChatClient {
        private var listener = KickConnectionListener {}
        override fun connect(configuration: KickConfiguration, listener: KickConnectionListener) { this.listener = listener }
        override fun disconnect() = listener.onEvent(KickConnectionEvent.PhaseChanged(KickConnectionPhase.Disconnected))
        override fun close() = Unit
        fun emit(event: KickConnectionEvent) = listener.onEvent(event)
    }
}
