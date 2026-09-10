package dev.rocky.core.ideas

import dev.rocky.core.live.LiveIdea
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownIdeaExporterTest {
    @Test
    fun exportsIdeasInDisplayOrder() {
        val ideas = listOf(
            LiveIdea("Criar uma série curta", "01:31", "CONTEÚDO"),
            LiveIdea("Fazer uma enquete", "01:05", "INTERAÇÃO"),
        )

        assertEquals(
            """
            # Rocky Ideas

            ## 01:31 · CONTEÚDO

            Criar uma série curta

            ## 01:05 · INTERAÇÃO

            Fazer uma enquete
            """.trimIndent() + "\n",
            ideasAsMarkdown(ideas),
        )
    }
}
