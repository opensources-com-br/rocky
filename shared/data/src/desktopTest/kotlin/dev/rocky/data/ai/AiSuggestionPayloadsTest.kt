package dev.rocky.data.ai

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

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

    @Test
    fun readsProviderTextAndValidatedSources() {
        val suggestionJson = """{"suggestion":"Responda sobre o preço.","source_message_ids":["m1","invented"]}"""
        val ollama = """{"message":{"content":${jsonString(suggestionJson)}}}"""
        val openAi = """{"output":[{"type":"reasoning"},{"content":[{"type":"output_text","text":${jsonString(suggestionJson)}}]}]}"""
        val openRouter = """{"choices":[{"message":{"content":${jsonString(suggestionJson)}}}]}"""
        val anthropic = """{"content":[{"type":"text","text":${jsonString(suggestionJson)}}]}"""
        val gemini = """{"candidates":[{"content":{"parts":[{"text":${jsonString(suggestionJson)}}]}}]}"""

        assertEquals(suggestionJson, AiSuggestionPayloads.ollamaText(ollama))
        assertEquals(suggestionJson, AiSuggestionPayloads.openAiText(openAi))
        assertEquals(suggestionJson, AiSuggestionPayloads.openRouterText(openRouter))
        assertEquals(suggestionJson, AiSuggestionPayloads.anthropicText(anthropic))
        assertEquals(suggestionJson, AiSuggestionPayloads.geminiText(gemini))
        assertEquals(setOf("m1"), AiSuggestionPayloads.suggestion(suggestionJson, setOf("m1"))?.sourceMessageIds)
    }

    @Test
    fun rejectsMissingEvidenceAndAcceptsEmptySuggestion() {
        assertFailsWith<IllegalArgumentException> {
            AiSuggestionPayloads.suggestion(
                """{"suggestion":"Sem fonte","source_message_ids":["invented"]}""",
                setOf("m1"),
            )
        }
        assertNull(
            AiSuggestionPayloads.suggestion(
                """{"suggestion":"","source_message_ids":[]}""",
                setOf("m1"),
            ),
        )
    }

    @Test
    fun explainsAnEmptyOpenRouterChoice() {
        val error = assertFailsWith<IllegalArgumentException> {
            AiSuggestionPayloads.openRouterText("""{"choices":[{"message":null}]}""")
        }

        assertEquals("OpenRouter retornou uma resposta vazia", error.message)
    }

    @Test fun joinsEveryOpenAiOutputTextItem() {
        val response = """{"output":[
            {"content":[{"type":"output_text","text":"primeira "}]},
            {"content":[{"type":"output_text","text":"segunda"}]}
        ]}"""
        assertEquals("primeira segunda", AiSuggestionPayloads.openAiText(response))
    }

    @Test fun explainsOpenAiResponseErrors() {
        val error = assertFailsWith<IllegalArgumentException> {
            AiSuggestionPayloads.openAiText("""{"error":{"message":"generation failed"}}""")
        }
        assertEquals("OpenAI: generation failed", error.message)
    }

    @Test fun identifiesGrokResponseErrors() {
        val error = assertFailsWith<IllegalArgumentException> {
            AiSuggestionPayloads.openAiText("""{"error":{"message":"generation failed"}}""", "Grok")
        }
        assertEquals("Grok: generation failed", error.message)
    }

    @Test fun detectsIncompleteOpenAiResponses() {
        val error = assertFailsWith<AiResponseIncompleteException> {
            AiSuggestionPayloads.openAiText("""{"incomplete_details":{"reason":"max_output_tokens"}}""")
        }
        assertEquals("max_output_tokens", error.reason)
    }

    @Test fun interpretsGeminiFinishReasons() {
        assertFailsWith<AiResponseIncompleteException> {
            AiSuggestionPayloads.geminiText("""{"candidates":[{"finishReason":"MAX_TOKENS"}]}""")
        }
        assertFailsWith<AiResponseBlockedException> {
            AiSuggestionPayloads.geminiText("""{"candidates":[{"finishReason":"SAFETY"}]}""")
        }
    }

    @Test fun detectsBlockedGeminiPrompts() {
        assertFailsWith<AiResponseBlockedException> {
            AiSuggestionPayloads.geminiText("""{"promptFeedback":{"blockReason":"BLOCKLIST"}}""")
        }
    }

    @Test fun detectsTruncatedClaudeResponses() {
        assertFailsWith<AiResponseIncompleteException> {
            AiSuggestionPayloads.anthropicText("""{"stop_reason":"max_tokens"}""")
        }
    }

    @Test fun detectsRefusedClaudeResponses() {
        assertFailsWith<AiResponseBlockedException> {
            AiSuggestionPayloads.anthropicText("""{"stop_reason":"refusal"}""")
        }
    }

    private fun jsonString(value: String): String = buildString {
        append('"')
        value.forEach { character ->
            when (character) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                else -> append(character)
            }
        }
        append('"')
    }
}
