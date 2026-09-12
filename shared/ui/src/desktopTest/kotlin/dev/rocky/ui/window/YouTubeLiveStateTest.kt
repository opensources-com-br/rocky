package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.youtube.YouTubeAccount
import dev.rocky.core.youtube.YouTubeBroadcast
import dev.rocky.core.youtube.YouTubeChatClient
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionEvent
import dev.rocky.core.youtube.YouTubeConnectionListener
import dev.rocky.core.youtube.YouTubeConnectionPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class YouTubeLiveStateTest {
    @Test fun receivesYouTubeMessagesAndMetrics() {
        var now = 10_000L
        val client = FakeYouTubeChatClient()
        val state = YouTubeLiveState(client) { now }
        state.connect(YouTubeConfiguration("client", "secret"))
        client.emit(YouTubeConnectionEvent.Connected(
            YouTubeAccount("channel", "Canal"),
            YouTubeBroadcast("video", "chat", "Live"),
        ))
        client.emit(YouTubeConnectionEvent.MessageReceived(
            ChatMessage("m1", "viewer", "Olá", StreamPlatform.YouTube),
        ))
        client.emit(YouTubeConnectionEvent.AudienceUpdated(42))

        assertTrue(state.isConnected)
        assertEquals("youtube-video-10000", state.sessionId)
        assertEquals("Olá", state.messages.single().text)
        assertEquals(1, state.messagesPerMinute)
        assertEquals(42, state.viewerCount)
        now += 120_000
        assertTrue(state.messagesReceivedWithin(60_000).isEmpty())
    }

    @Test fun disconnectsAndClearsTheActiveBroadcast() {
        val client = FakeYouTubeChatClient()
        val state = YouTubeLiveState(client) { 1L }
        state.connect(YouTubeConfiguration("client", "secret"))
        client.emit(YouTubeConnectionEvent.PhaseChanged(YouTubeConnectionPhase.Disconnected))
        assertEquals(YouTubeConnectionPhase.Disconnected, state.phase)
        assertEquals(null, state.broadcast)
    }

    private class FakeYouTubeChatClient : YouTubeChatClient {
        private var listener = YouTubeConnectionListener {}
        override fun connect(configuration: YouTubeConfiguration, listener: YouTubeConnectionListener) {
            this.listener = listener
        }
        override fun disconnect() = listener.onEvent(
            YouTubeConnectionEvent.PhaseChanged(YouTubeConnectionPhase.Disconnected),
        )
        override fun close() = Unit
        fun emit(event: YouTubeConnectionEvent) = listener.onEvent(event)
    }
}
