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

    @Test
    fun upgradesAnExistingNotesDatabase() {
        val databasePath = Files.createTempDirectory("rocky-notes-migration-test").resolve("notes.db")
        val driver = app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver(
            "jdbc:sqlite:${databasePath.toAbsolutePath()}",
        )
        driver.execute(
            null,
            """CREATE TABLE note (
                id TEXT NOT NULL PRIMARY KEY,
                text TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                tag TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )""".trimIndent(),
            0,
            null,
        ).value
        driver.execute(null, "INSERT INTO note VALUES ('legacy', 'Texto', 'agora', 'SUGESTÃO', 1)", 0, null).value
        driver.execute(null, "PRAGMA user_version = 1", 0, null).value
        driver.close()

        SqliteNoteRepository(databasePath).use { repository ->
            val note = repository.getAll().single()
            assertEquals("legacy", note.id)
            assertTrue(note.sourceMessageIds.isEmpty())
            assertTrue(note.evidence.isEmpty())
        }
    }

}
