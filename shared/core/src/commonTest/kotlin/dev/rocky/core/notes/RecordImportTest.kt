package dev.rocky.core.notes

import dev.rocky.core.live.LiveNote
import kotlin.test.Test
import kotlin.test.assertEquals

class RecordImportTest {
    @Test fun skipsExactDuplicatesAndKeepsIncomingOrder() {
        val existing = LiveNote("old", "saved", "now", "NOTA")
        val first = LiveNote("first", "one", "now", "NOTA")
        val second = LiveNote("second", "two", "now", "IDEIA")

        assertEquals(listOf(first, second), recordsToImport(listOf(existing), listOf(first, existing, second)))
    }
}
