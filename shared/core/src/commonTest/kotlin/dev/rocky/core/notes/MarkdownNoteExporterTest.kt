package dev.rocky.core.notes

import dev.rocky.core.live.LiveNote
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownNoteExporterTest {
    @Test
    fun exportsNotesInDisplayOrder() {
        val notes = listOf(
            LiveNote(
                "1",
                "Primeira ideia",
                "01:24",
                "IDEIA",
                evidence = listOf("ana: Quero saber o preço"),
            ),
            LiveNote("2", "Uma pendência", "01:38", "PENDÊNCIA"),
        )

        assertEquals(
            """
            # Rocky Notes

            ## 01:24 · IDEIA

            Primeira ideia

            ### Evidence

            - ana: Quero saber o preço

            ## 01:38 · PENDÊNCIA

            Uma pendência
            """.trimIndent() + "\n",
            notesAsMarkdown(notes),
        )
    }
}
