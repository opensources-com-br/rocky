package dev.rocky.ui.window

import dev.rocky.core.live.LiveNote
import org.junit.Assert.*
import org.junit.Test

class LocalNotesStateTest {
    @Test fun explainsAnUnavailableExportDestination() {
        val state = LocalNotesState(TransientNoteRepository())

        state.export { error("read only") }

        assertEquals("Não foi possível exportar. Escolha uma pasta disponível e tente novamente.", state.notice)
    }

    @Test fun preservesLocalRecordsWhenWritesFail() {
        val note = LiveNote("saved", "Original", "now", "NOTA")
        val repository = object : dev.rocky.core.notes.NoteRepository {
            override fun getAll() = listOf(note)
            override fun save(note: LiveNote) = error("read only")
            override fun update(note: LiveNote) = error("read only")
            override fun delete(noteId: String) = error("read only")
        }
        val state = LocalNotesState(repository)

        assertFalse(state.update(note.copy(text = "Changed")))
        assertFalse(state.delete(note.id))
        assertEquals(listOf(note), state.notes)
    }

    @Test fun deduplicatesSavesAndUndoesOnlyTheLatestSave() {
        val repository = TransientNoteRepository()
        val state = LocalNotesState(repository)
        val first = LiveNote("first", "Primeira", "now", "MANUAL")
        val second = LiveNote("second", "Segunda", "now", IDEA_TAG)
        assertTrue(state.save(first))
        assertTrue(state.save(first))
        assertEquals(1, repository.getAll().size)
        state.save(second)
        state.undoSave()
        assertEquals(listOf(first), repository.getAll())
        assertNull(state.undoSaveId)
        state.undoSave()
        assertEquals(listOf(first), repository.getAll())
    }
}
