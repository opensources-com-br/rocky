package dev.rocky.data.ai

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AiSuggestionPayloadsTest {
    @Test fun acceptsAnExplanationWhenNoRecentChatExists() {
        val answer = AiSuggestionPayloads.suggestion(
            """{"suggestion":"Não há mensagens recentes para consultar.","source_message_ids":[]}""", emptySet(),
        )
        assertEquals(emptySet(), answer?.sourceMessageIds)
        assertFailsWith<IllegalArgumentException> {
            AiSuggestionPayloads.suggestion(
                """{"suggestion":"Inventada","source_message_ids":["fake"]}""", emptySet(),
            )
        }
    }
}
