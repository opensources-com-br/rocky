package dev.rocky.ui.window

import androidx.compose.runtime.*
import dev.rocky.core.live.*

internal class LiveWorkspace(private val records: LocalNotesState) {
    var sessionId by mutableStateOf(""); private set
    var label by mutableStateOf(""); private set
    private var startedAt = 0L
    var summary by mutableStateOf<String?>(null)
    val questions = QuestionQueue(records)
    fun start(id: String, name: String, start: Long) {
        if (sessionId == id) return
        sessionId = id; label = name; startedAt = start
    }
    fun offset(now: Long) = (now - startedAt).coerceAtLeast(0)
    fun decorate(note: LiveNote, now: Long): LiveNote = if (sessionId.isBlank()) note else
        note.copy(sessionId = sessionId, sessionLabel = label, offsetMillis = offset(now))
    fun finish(timestamp: String): Boolean {
        if (sessionId.isBlank()) return true
        val text = summarizeRecords(records.notes, sessionId, label)
        if (!records.putRecord(LiveNote("summary-$sessionId", text, timestamp, SUMMARY_TAG,
                sessionId = sessionId, sessionLabel = label))) return false
        summary = text; sessionId = ""
        return true
    }
}
