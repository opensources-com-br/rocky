package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionListener
import dev.rocky.core.twitch.TwitchConnectionPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TwitchLiveStateSoakTest {
    @Test
    fun keepsAProlongedSessionBoundedAcrossConnectionDrops() {
        val client = SoakTwitchChatClient()
        val state = TwitchLiveState(client)
        state.connect("client-id")
        client.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))

        repeat(SIMULATED_MESSAGE_COUNT) { index ->
            if (index > 0 && index % DROP_INTERVAL == 0) {
                client.emit(
                    TwitchConnectionEvent.PhaseChanged(
                        TwitchConnectionPhase.Reconnecting,
                        "Simulated network drop",
                    ),
                )
                client.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            }
            client.emit(
                TwitchConnectionEvent.MessageReceived(
                    ChatMessage("message-$index", "viewer", "Message $index", StreamPlatform.Twitch),
                ),
            )
        }

        assertEquals(TwitchConnectionPhase.Connected, state.phase)
        assertTrue(state.isRealSession)
        assertEquals(TwitchLiveState.MAX_CHAT_MESSAGES, state.messages.size)
        assertEquals("message-19000", state.messages.first().id)
        assertEquals("message-19999", state.messages.last().id)
    }

    private class SoakTwitchChatClient : TwitchChatClient {
        private var listener = TwitchConnectionListener {}

        override fun connect(clientId: String, listener: TwitchConnectionListener) {
            this.listener = listener
        }

        override fun disconnect() = Unit
        override fun close() = Unit

        fun emit(event: TwitchConnectionEvent) = listener.onEvent(event)
    }

    private companion object {
        const val SIMULATED_MESSAGE_COUNT = 20_000
        const val DROP_INTERVAL = 2_500
    }
}
