package dev.rocky.ui.window

import dev.rocky.core.live.*

internal class QuestionQueue(private val records: LocalNotesState) {
    private val seen = linkedSetOf<String>()
    private var session = ""
    fun collect(messages: List<ChatMessage>, sessionId: String, label: String, timestamp: String, offset: Long) {
        if (session != sessionId) { seen.clear(); session = sessionId }
        if (sessionId.isBlank()) return
        messages.filter { it.id !in seen && isChatQuestion(it.text) }.forEach { message ->
            val existing = records.notes.firstOrNull {
                it.tag == QUESTION_TAG && it.sessionId == sessionId && similarQuestion(it.text, message.text)
            }
            if (existing == null && records.notes.count { it.tag == QUESTION_TAG && it.sessionId == sessionId } >= 100) return@forEach
            val record = existing?.copy(
                sourceMessageIds = (existing.sourceMessageIds + message.id).takeLastSet(50),
                evidence = (existing.evidence + messageEvidence(message)).takeLast(20),
                messageCount = existing.messageCount + 1,
            ) ?: LiveNote("question-${message.sessionId}-${message.id}", message.text.take(1000), timestamp,
                QUESTION_TAG, setOf(message.id), listOf(messageEvidence(message)), sessionId, label, offset,
                messageCount = 1)
            if (records.putRecord(record)) seen.add(message.id)
        }
        // Keep a bounded deduplication window matching the in-memory chat buffer.
        seen.retainAll(messages.map { it.id }.toSet())
    }
}
private fun Set<String>.takeLastSet(size: Int) = toList().takeLast(size).toSet()
