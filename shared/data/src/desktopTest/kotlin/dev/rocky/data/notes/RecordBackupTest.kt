package dev.rocky.data.notes

import dev.rocky.core.live.LiveNote
import java.nio.file.Files
import kotlin.test.*

class RecordBackupTest {
    private val note = LiveNote("n", "ideia", "now", "IDEIA", setOf("m"), listOf("viewer: hi"),
        "session", "Live de teste", 42000, true, 3)

    @Test fun preservesAllMetadataAcrossBackupAndDatabaseReopen() {
        assertEquals(listOf(note), decodeRecordBackup(encodeRecordBackup(listOf(note))))
        val directory = Files.createTempDirectory("rocky-backup-test")
        try {
            val path = directory.resolve("notes.db")
            SqliteNoteRepository(path).use { it.save(note) }
            SqliteNoteRepository(path).use { assertEquals(listOf(note), it.getAll()) }
        } finally { directory.toFile().deleteRecursively() }
    }

    @Test fun importsOnceAndRejectsConflictsWithoutPartialWrites() {
        val directory = Files.createTempDirectory("rocky-import-test")
        try {
            SqliteNoteRepository(directory.resolve("notes.db")).use { repo ->
                assertEquals(1, repo.importNotes(listOf(note)))
                assertEquals(0, repo.importNotes(listOf(note)))
                assertFails { repo.importNotes(listOf(note.copy(id = "new"), note.copy(text = "conflict"))) }
                assertEquals(listOf(note), repo.getAll())
            }
        } finally { directory.toFile().deleteRecursively() }
    }

    @Test fun rejectsUnsupportedOrInvalidBackups() {
        val encoded = encodeRecordBackup(listOf(note))
        assertFails { decodeRecordBackup(encoded.replace("\"version\":1", "\"version\":999")) }
        assertFails { decodeRecordBackup("{}") }
        assertFails { encodeRecordBackup(listOf(note, note)) }
        assertFails { encodeRecordBackup(listOf(note.copy(offsetMillis = -1))) }
    }
}
