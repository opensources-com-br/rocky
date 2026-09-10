package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.RockySuggestion
import dev.rocky.core.live.StreamPlatform
import org.junit.Assert.assertEquals
import org.junit.Test

class SuggestionNoteTest {
    @Test
    fun preservesTimestampAndCitedMessages() {
        val messages = listOf(
            ChatMessage("m1", "ana", "Qual é o preço?", StreamPlatform.Twitch),
            ChatMessage("m2", "bia", "Também quero saber", StreamPlatform.Twitch),
        )

        val note = suggestionNote(
            RockySuggestion("s1", "O chat perguntou o preço.", setOf("m2")),
            messages,
            "14:35",
        )

        assertEquals("14:35", note.timestamp)
        assertEquals(setOf("m2"), note.sourceMessageIds)
        assertEquals(listOf("bia: Também quero saber"), note.evidence)
    }
}
