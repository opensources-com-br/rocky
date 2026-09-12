package dev.rocky.data.notes

import java.nio.file.Files
import kotlin.test.*

class RecoverableNoteRepositoryTest {
    @Test fun preservesCorruptDatabaseAndRetriesWhenStorageIsRepaired() {
        val directory = Files.createTempDirectory("rocky-recovery")
        val path = directory.resolve("notes.db")
        val original = "invalid database".repeat(100)
        Files.writeString(path, original)
        RecoverableNoteRepository(path).use { repository ->
            assertFails { repository.getAll() }
            assertEquals(original, Files.readString(path))
            // Simulates an explicit user repair, not automatic deletion by the app.
            Files.move(path, directory.resolve("preserved.db"))
            assertTrue(repository.getAll().isEmpty())
            assertEquals(original, Files.readString(directory.resolve("preserved.db")))
        }
        directory.toFile().deleteRecursively()
    }
}
