package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.LiveEvent
import dev.rocky.core.live.LiveSessionMode
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.live.RockySuggestion
import dev.rocky.core.live.StreamPlatform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedLiveStateTest {
    @Test
    fun controlsSessionLifecycle() {
        val state = SimulatedLiveState()

        assertEquals(LiveSessionMode.Demonstration, state.mode)
        assertEquals(LiveSessionStatus.Stopped, state.status)
        state.receive(LiveEvent.MessageReceived(message))
        assertTrue(state.messages.isEmpty())

        state.start()
        state.receive(LiveEvent.MessageReceived(message))
        state.receive(LiveEvent.SuggestionCreated(suggestion))
        val savedNote = state.createNoteFromSuggestion()
        assertEquals(suggestion.text, savedNote?.text)
        state.markSuggestionSaved()
        assertTrue(state.suggestionSaved)

        state.end()
        assertEquals(LiveSessionStatus.Ended, state.status)

        state.restart()
        assertEquals(LiveSessionStatus.Running, state.status)
        assertTrue(state.messages.isEmpty())
        assertNull(state.suggestion)
        assertEquals(2, state.sessionNumber)
    }

    private val message = ChatMessage(
        id = "message",
        author = "viewer",
        text = "Uma mensagem simulada",
        platform = StreamPlatform.Twitch,
    )

    private val suggestion = RockySuggestion(
        id = "suggestion",
        text = "Uma sugestão simulada",
        sourceMessageIds = setOf(message.id),
    )
}
