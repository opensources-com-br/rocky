package dev.rocky.ui.window

import dev.rocky.core.live.*
import org.junit.Test
import org.junit.Assert.*

class QuestionQueueTest {
    private fun msg(id: String, text: String = "Qual próximo jogo?") =
        ChatMessage(id, "viewer$id", text, StreamPlatform.Twitch, sessionId = "live")
    @Test fun groupsQuestionsAndPreservesAnsweredStateAcrossFilterChanges() {
        val records = LocalNotesState(TransientNoteRepository())
        val queue = QuestionQueue(records)
        val messages = listOf(msg("1"), msg("2", "Qual é o próximo jogo?"))
        queue.collect(messages, "live", "Live", "now", 1000)
        val question = records.notes.single()
        assertEquals(2, question.messageCount)
        records.update(question.copy(completed = true))
        queue.collect(emptyList(), "live", "Live", "now", 2000)
        queue.collect(messages + msg("3"), "live", "Live", "now", 3000)
        assertEquals(3, records.notes.single().messageCount)
        assertTrue(records.notes.single().completed)
        assertEquals(3, records.notes.single().sourceMessageIds.size)
    }
    @Test fun separatesLivesAndBoundsTheQueue() {
        val records = LocalNotesState(TransientNoteRepository())
        val queue = QuestionQueue(records)
        val messages = (1..105).map { msg("$it", "topic$it?") }
        queue.collect(messages, "live", "Live", "now", 0)
        assertEquals(100, records.notes.size)
        queue.collect(listOf(msg("new")), "other", "Other", "now", 0)
        assertEquals(1, records.notes.count { it.sessionId == "other" })
    }
}
