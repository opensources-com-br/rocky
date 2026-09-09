package dev.rocky.ui.window

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dev.rocky.core.live.LiveNote
import dev.rocky.core.notes.NoteRepository

internal class LocalNotesState(private val repository: NoteRepository) {
    val notes = mutableStateListOf<LiveNote>()

    var notice by mutableStateOf<String?>(null)
        private set

    init {
        runCatching(repository::getAll)
            .onSuccess(notes::addAll)
            .onFailure { notice = "Não foi possível carregar as notas locais." }
    }

    fun save(note: LiveNote): Boolean = persist(
        action = { repository.save(note) },
        onSuccess = { notes.add(0, note) },
        successNotice = "Nota salva localmente.",
    )

    fun update(note: LiveNote): Boolean = persist(
        action = { repository.update(note) },
        onSuccess = {
            val index = notes.indexOfFirst { it.id == note.id }
            if (index >= 0) notes[index] = note
        },
        successNotice = "Nota atualizada.",
    )

    fun delete(noteId: String): Boolean = persist(
        action = { repository.delete(noteId) },
        onSuccess = { notes.removeAll { it.id == noteId } },
        successNotice = "Nota excluída.",
    )

    fun setExportResult(exported: Boolean) {
        if (exported) notice = "Markdown exportado."
    }

    private fun persist(
        action: () -> Unit,
        onSuccess: () -> Unit,
        successNotice: String,
    ): Boolean = runCatching(action).fold(
        onSuccess = {
            onSuccess()
            notice = successNotice
            true
        },
        onFailure = {
            notice = "Não foi possível salvar a alteração."
            false
        },
    )
}

internal class TransientNoteRepository : NoteRepository {
    private val notes = mutableListOf<LiveNote>()

    override fun getAll(): List<LiveNote> = notes.toList()

    override fun save(note: LiveNote) {
        notes.add(0, note)
    }

    override fun update(note: LiveNote) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index >= 0) notes[index] = note
    }

    override fun delete(noteId: String) {
        notes.removeAll { it.id == noteId }
    }
}
