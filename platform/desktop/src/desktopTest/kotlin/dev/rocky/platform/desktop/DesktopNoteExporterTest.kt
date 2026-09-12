package dev.rocky.platform.desktop

import java.nio.file.Files
import kotlin.test.*

class DesktopNoteExporterTest {
    @Test fun replacesCompleteFileAndCleansUpFailedExports() {
        val directory = Files.createTempDirectory("rocky-export-test")
        try {
            val target = directory.resolve("notes.md")
            Files.writeString(target, "before")
            writeMarkdownAtomically(target, "after")
            assertEquals("after", Files.readString(target))
            val unavailable = Files.createDirectory(directory.resolve("unavailable.md"))
            Files.writeString(unavailable.resolve("preserved"), "original")
            assertFails { writeMarkdownAtomically(unavailable, "new") }
            assertEquals("original", Files.readString(unavailable.resolve("preserved")))
            Files.list(directory).use { assertEquals(2, it.count().toInt()) }
        } finally { directory.toFile().deleteRecursively() }
    }
}
