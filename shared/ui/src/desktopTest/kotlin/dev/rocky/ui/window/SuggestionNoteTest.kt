package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.RockySuggestion
import dev.rocky.core.live.StreamPlatform
import org.junit.Assert.assertEquals
import org.junit.Test

class SuggestionNoteTest {
    @Test fun preservesSessionAndSourceMetadataInExportedEvidence() {
        val message = ChatMessage("m1", "ana", "Dúvida", StreamPlatform.Twitch,
            authorId = "u1", channelId = "c1", sourceTimestamp = "2026-09-12T12:00:00Z",
            receivedAtMillis = 1234, sessionId = "session1")
        val note = suggestionNote(RockySuggestion("s1", "Resposta", setOf("m1")), listOf(message), "now")
        val markdown = dev.rocky.core.notes.notesAsMarkdown(listOf(note))
        for (value in listOf("message=m1", "author=u1", "channel=c1", "session=session1", "source=2026-09-12T12:00:00Z")) {
            org.junit.Assert.assertTrue(markdown.contains(value))
        }
    }

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
