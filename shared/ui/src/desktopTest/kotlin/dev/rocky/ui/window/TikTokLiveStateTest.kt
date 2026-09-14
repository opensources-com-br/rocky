package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.tiktok.TikTokAccount
import dev.rocky.core.tiktok.TikTokChatClient
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.core.tiktok.TikTokConnectionEvent
import dev.rocky.core.tiktok.TikTokConnectionListener
import dev.rocky.core.tiktok.TikTokConnectionPhase
import dev.rocky.core.tiktok.TikTokLiveRoom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TikTokLiveStateTest {
    @Test
    fun tracksTikTokMessagesAudienceAndSession() {
        var now = 10_000L
        val client = FakeTikTokChatClient()
        val state = TikTokLiveState(client) { now }

        state.connect(TikTokConfiguration("rocky_live"))
        client.emit(TikTokConnectionEvent.Connected(
            TikTokAccount("rocky_live", "Rocky"), TikTokLiveRoom("room-1", "Minha live"),
        ))
        client.emit(TikTokConnectionEvent.AudienceUpdated(42))
        client.emit(TikTokConnectionEvent.MessageReceived(
            ChatMessage("m1", "Ana", "Olá", StreamPlatform.TikTok),
        ))

        assertEquals(TikTokConnectionPhase.Connected, state.phase)
        assertEquals(42, state.viewerCount)
        assertEquals("tiktok-room-1-10000", state.sessionId)
        assertEquals(state.sessionId, state.messages.single().sessionId)
        assertEquals(1, state.messagesPerMinute)

        now += 61_000
        state.refreshMetrics()
        assertEquals(0, state.messagesPerMinute)
        assertTrue(state.pulse.isNotEmpty())
    }

    @Test
    fun clearsConnectionWhenDisconnected() {
        val client = FakeTikTokChatClient()
        val state = TikTokLiveState(client) { 10_000L }
        state.connect(TikTokConfiguration("rocky_live"))
        client.emit(TikTokConnectionEvent.Connected(
            TikTokAccount("rocky_live", "Rocky"), TikTokLiveRoom("room-1", "Minha live"),
        ))

        client.emit(TikTokConnectionEvent.PhaseChanged(TikTokConnectionPhase.Disconnected))

        assertEquals(TikTokConnectionPhase.Disconnected, state.phase)
        assertEquals(null, state.account)
        assertEquals(null, state.room)
        assertEquals(null, state.viewerCount)
    }
}

private class FakeTikTokChatClient : TikTokChatClient {
    private var listener = TikTokConnectionListener {}
    override fun connect(configuration: TikTokConfiguration, listener: TikTokConnectionListener) {
        this.listener = listener
        listener.onEvent(TikTokConnectionEvent.PhaseChanged(TikTokConnectionPhase.Connecting))
    }
    override fun disconnect() = listener.onEvent(TikTokConnectionEvent.PhaseChanged(TikTokConnectionPhase.Disconnected))
    override fun close() = Unit
    fun emit(event: TikTokConnectionEvent) = listener.onEvent(event)
}
