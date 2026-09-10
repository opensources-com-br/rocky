package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionListener
import dev.rocky.core.twitch.TwitchConnectionPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TwitchLiveStateTest {
    @Test
    fun followsAuthorizationConnectionAndChatEvents() {
        val client = FakeTwitchChatClient()
        val state = TwitchLiveState(client)

        state.connect("client-id")
        client.emit(TwitchConnectionEvent.AuthorizationRequired("ABCD-1234", "https://example.test"))

        assertEquals(TwitchConnectionPhase.AwaitingAuthorization, state.phase)
        assertEquals("ABCD-1234", state.userCode)

        client.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
        client.emit(
            TwitchConnectionEvent.MessageReceived(
                ChatMessage("message-1", "viewer", "Olá!", StreamPlatform.Twitch),
            ),
        )

        assertEquals(TwitchConnectionPhase.Connected, state.phase)
        assertEquals("rocky_live", state.account?.login)
        assertEquals("Olá!", state.messages.single().text)
        assertTrue(state.isRealSession)

        state.disconnect()
        assertFalse(state.isRealSession)
    }

    @Test
    fun keepsOnlyTheMostRecentChatMessages() {
        val client = FakeTwitchChatClient()
        val state = TwitchLiveState(client)
        state.connect("client-id")

        repeat(TwitchLiveState.MAX_CHAT_MESSAGES + 1) { index ->
            client.emit(
                TwitchConnectionEvent.MessageReceived(
                    ChatMessage("message-$index", "viewer", "Message $index", StreamPlatform.Twitch),
                ),
            )
        }

        assertEquals(TwitchLiveState.MAX_CHAT_MESSAGES, state.messages.size)
        assertEquals("message-1", state.messages.first().id)
        assertEquals("message-1000", state.messages.last().id)
    }

    @Test
    fun calculatesMessageRateAndSessionTotal() {
        var now = 100_000L
        val client = FakeTwitchChatClient()
        val state = TwitchLiveState(client) { now }
        state.connect("client-id")

        client.emit(message("m1"))
        now += 30_000
        client.emit(message("m2"))
        assertEquals(2, state.messagesPerMinute)
        assertEquals(2, state.totalMessages)

        now += 31_000
        state.refreshMetrics()
        assertEquals(1, state.messagesPerMinute)
        assertEquals(2, state.totalMessages)
    }

    @Test
    fun ignoresEventsFromAPreviousConnection() {
        val client = FakeTwitchChatClient()
        val state = TwitchLiveState(client)
        state.connect("first-client")
        val firstConnection = client.currentConnection

        state.connect("second-client")
        firstConnection.onEvent(
            TwitchConnectionEvent.MessageReceived(
                ChatMessage("stale", "viewer", "Old message", StreamPlatform.Twitch),
            ),
        )
        client.emit(
            TwitchConnectionEvent.MessageReceived(
                ChatMessage("current", "viewer", "New message", StreamPlatform.Twitch),
            ),
        )

        assertEquals(listOf("current"), state.messages.map(ChatMessage::id))
    }

    @Test
    fun ignoresRecoveryEventsAfterManualDisconnect() {
        val client = FakeTwitchChatClient()
        val state = TwitchLiveState(client)
        state.connect("client-id")
        val interruptedConnection = client.currentConnection
        client.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
        client.emit(
            TwitchConnectionEvent.PhaseChanged(
                TwitchConnectionPhase.Reconnecting,
                "Simulated network drop",
            ),
        )

        state.disconnect()
        interruptedConnection.onEvent(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
        interruptedConnection.onEvent(
            TwitchConnectionEvent.MessageReceived(
                ChatMessage("stale", "viewer", "Late message", StreamPlatform.Twitch),
            ),
        )

        assertEquals(TwitchConnectionPhase.Disconnected, state.phase)
        assertFalse(state.isRealSession)
        assertTrue(state.messages.isEmpty())
    }

    private class FakeTwitchChatClient : TwitchChatClient {
        private var listener = TwitchConnectionListener {}
        val currentConnection: TwitchConnectionListener
            get() = listener

        override fun connect(clientId: String, listener: TwitchConnectionListener) {
            this.listener = listener
        }

        override fun disconnect() {
            listener.onEvent(TwitchConnectionEvent.PhaseChanged(TwitchConnectionPhase.Disconnected))
        }

        override fun close() = Unit

        fun emit(event: TwitchConnectionEvent) = listener.onEvent(event)
    }

    private fun message(id: String) = TwitchConnectionEvent.MessageReceived(
        ChatMessage(id, "viewer", "Message $id", StreamPlatform.Twitch),
    )
}
