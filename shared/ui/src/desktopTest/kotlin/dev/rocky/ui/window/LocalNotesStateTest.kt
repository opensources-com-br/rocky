package dev.rocky.ui.window

import dev.rocky.core.live.LiveNote
import org.junit.Assert.*
import org.junit.Test

class LocalNotesStateTest {
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
