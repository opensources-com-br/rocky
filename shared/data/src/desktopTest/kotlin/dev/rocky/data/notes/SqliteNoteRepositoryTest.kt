package dev.rocky.data.notes

import dev.rocky.core.live.LiveNote
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqliteNoteRepositoryTest {
    @Test
    fun savesUpdatesDeletesAndReopensNotes() {
        val databasePath = Files.createTempDirectory("rocky-notes-test").resolve("notes.db")
        val original = LiveNote(
            "note-1",
            "Texto original",
            "14:35",
            "SUGESTÃO",
            sourceMessageIds = setOf("message-1"),
            evidence = listOf("viewer: Qual é o preço?"),
        )

        SqliteNoteRepository(databasePath).use { repository ->
            repository.save(original)
        }

        SqliteNoteRepository(databasePath).use { repository ->
            assertEquals(listOf(original), repository.getAll())
            repository.update(original.copy(text = "Texto editado"))
        }

        SqliteNoteRepository(databasePath).use { repository ->
            assertEquals("Texto editado", repository.getAll().single().text)
            repository.delete(original.id)
            assertTrue(repository.getAll().isEmpty())
        }
    }

}
