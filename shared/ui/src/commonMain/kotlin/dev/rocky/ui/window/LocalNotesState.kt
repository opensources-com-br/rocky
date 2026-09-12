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

    var loadFailed by mutableStateOf(false)
        private set

    var undoSaveId by mutableStateOf<String?>(null)
        private set

    fun undoSave() {
        val id = undoSaveId ?: return
        if (delete(id)) {
            undoSaveId = null
            notice = "Salvamento desfeito."
        }
    }

    init { reload() }

    fun reload() {
        runCatching(repository::getAll)
            .onSuccess { loaded ->
                notes.clear(); notes.addAll(loaded)
                loadFailed = false
                notice = null
            }
            .onFailure {
                loadFailed = true
                notice = "Não foi possível abrir o banco de notas. Verifique a pasta de dados e tente carregar novamente. O arquivo original foi preservado."
            }
    }

    fun save(note: LiveNote): Boolean {
        if (notes.any { it.id == note.id }) {
            undoSaveId = null
            notice = "Este item já foi salvo."
            return true
        }
        return persist(
            action = { repository.save(note) },
            onSuccess = { notes.add(0, note); undoSaveId = note.id },
            successNotice = if (note.tag == IDEA_TAG) "Ideia salva localmente." else "Nota salva localmente.",
        )
    }

    fun putRecord(note: LiveNote): Boolean = runCatching {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index < 0) { repository.save(note); notes.add(0, note) }
        else { repository.update(note); notes[index] = note }
    }.fold({ true }, { notice = "Não foi possível salvar a alteração."; false })

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
        onSuccess = { notes.removeAll { it.id == noteId }; if (undoSaveId == noteId) undoSaveId = null },
        successNotice = "Nota excluída.",
    )

    fun deleteAll(): Boolean = persist(
        action = { repository.deleteAll() },
        onSuccess = { notes.clear(); undoSaveId = null },
        successNotice = "Todas as notas foram apagadas.",
    )

    fun importRecords(incoming: List<LiveNote>): Boolean = runCatching {
        val count = repository.importNotes(incoming)
        reload()
        check(!loadFailed)
        undoSaveId = null
        notice = "$count registros importados."
    }.fold({ true }, { notice = it.message ?: "Não foi possível importar os registros."; false })

    fun export(exporter: (List<LiveNote>) -> Boolean) {
        runCatching { exporter(notes.toList()) }
            .onSuccess(::setExportResult)
            .onFailure { notice = "Não foi possível exportar. Escolha uma pasta disponível e tente novamente." }
    }

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
    override fun importNotes(notes: List<LiveNote>): Int {
        val fresh = dev.rocky.core.notes.recordsToImport(this.notes, notes)
        this.notes.addAll(0, fresh)
        return fresh.size
    }

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
