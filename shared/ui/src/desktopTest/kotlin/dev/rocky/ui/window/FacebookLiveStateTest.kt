package dev.rocky.ui.window

import dev.rocky.core.facebook.FacebookChatClient
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionEvent
import dev.rocky.core.facebook.FacebookConnectionListener
import dev.rocky.core.facebook.FacebookConnectionPhase
import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.facebook.FacebookPage
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FacebookLiveStateTest {
    @Test fun receivesFacebookMessagesAndMetrics() {
        var now = 10_000L
        val client = FakeFacebookChatClient()
        val state = FacebookLiveState(client) { now }
        state.connect(FacebookConfiguration("app", "secret"))
        client.emit(FacebookConnectionEvent.Connected(
            FacebookPage("page", "Página Rocky"),
            FacebookLiveVideo("live", "Live"),
        ))
        client.emit(FacebookConnectionEvent.MessageReceived(
            ChatMessage("m1", "viewer", "Olá", StreamPlatform.Facebook),
        ))
        client.emit(FacebookConnectionEvent.AudienceUpdated(42))

        assertTrue(state.isConnected)
        assertEquals("facebook-live-10000", state.sessionId)
        assertEquals("Olá", state.messages.single().text)
        assertEquals(1, state.messagesPerMinute)
        assertEquals(42, state.viewerCount)
        now += 120_000
        assertTrue(state.messagesReceivedWithin(60_000).isEmpty())
    }

    @Test fun disconnectsAndClearsTheActiveLive() {
        val client = FakeFacebookChatClient()
        val state = FacebookLiveState(client) { 1L }
        state.connect(FacebookConfiguration("app", "secret"))
        client.emit(FacebookConnectionEvent.PhaseChanged(FacebookConnectionPhase.Disconnected))
        assertEquals(FacebookConnectionPhase.Disconnected, state.phase)
        assertEquals(null, state.liveVideo)
    }

    @Test fun keepsOnlyRecentFacebookMessages() {
        val client = FakeFacebookChatClient()
        val state = FacebookLiveState(client) { 1L }
        state.connect(FacebookConfiguration("app", "secret"))
        repeat(1_001) { client.emit(FacebookConnectionEvent.MessageReceived(
            ChatMessage("m$it", "viewer", "Olá", StreamPlatform.Facebook))) }
        assertEquals(1_000, state.messages.size)
        assertEquals("m1", state.messages.first().id)
    }

    private class FakeFacebookChatClient : FacebookChatClient {
        private var listener = FacebookConnectionListener {}
        override fun connect(configuration: FacebookConfiguration, listener: FacebookConnectionListener) {
            this.listener = listener
        }
        override fun disconnect() = listener.onEvent(
            FacebookConnectionEvent.PhaseChanged(FacebookConnectionPhase.Disconnected),
        )
        override fun close() = Unit
        fun emit(event: FacebookConnectionEvent) = listener.onEvent(event)
    }
}
