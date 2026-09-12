package dev.rocky.data.notes

import dev.rocky.core.live.LiveNote
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqliteNoteRepositoryTest {
    @Test fun clearsNotesWithoutTouchingAnExport() {
        val directory = Files.createTempDirectory("rocky-delete-notes")
        val path = directory.resolve("notes.db")
        val exported = directory.resolve("export.md")
        Files.writeString(exported, "preserved export")
        SqliteNoteRepository(path).use { repository ->
            repository.save(LiveNote("n1", "note", "now", "test"))
            repository.deleteAll()
        }
        SqliteNoteRepository(path).use { assertTrue(it.getAll().isEmpty()) }
        assertEquals("preserved export", Files.readString(exported))
        directory.toFile().deleteRecursively()
    }

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

    @Test
    fun removesLegacyDemoNotesWithoutRemovingRealNotes() {
        val databasePath = Files.createTempDirectory("rocky-notes-cleanup-test").resolve("notes.db")
        val legacyDemoNote = LiveNote(
            "legacy-demo",
            "Sete pessoas perguntaram o preço do curso nos últimos dois minutos. Vale responder agora.",
            "agora",
            "SUGESTÃO",
        )
        val realNote = legacyDemoNote.copy(
            id = "real-note",
            timestamp = "2026-09-11 21:00:00 -03:00",
            tag = "SUGESTÃO IA",
            sourceMessageIds = setOf("message-1"),
            evidence = listOf("viewer: Qual é o preço?"),
        )

        SqliteNoteRepository(databasePath).use { repository ->
            repository.save(legacyDemoNote)
            repository.save(realNote)
        }

        SqliteNoteRepository(databasePath).use { repository ->
            assertEquals(listOf(realNote), repository.getAll())
        }
    }

}
