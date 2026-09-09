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

    private class FakeTwitchChatClient : TwitchChatClient {
        private var listener = TwitchConnectionListener {}

        override fun connect(clientId: String, listener: TwitchConnectionListener) {
            this.listener = listener
        }

        override fun disconnect() {
            listener.onEvent(TwitchConnectionEvent.PhaseChanged(TwitchConnectionPhase.Disconnected))
        }

        override fun close() = Unit

        fun emit(event: TwitchConnectionEvent) = listener.onEvent(event)
    }
}
