package dev.rocky.ui.window

import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiSuggestionStateTest {
    @Test
    fun batchesAutomaticAnalysisAndResetsForANewSession() = runBlocking {
        val client = FakeAiSuggestionClient()
        val state = AiSuggestionState(client, ollamaConfiguration) {}

        state.analyze(this, messages(2), automatic = true)
        state.analyze(this, messages(3), automatic = true)
        while (state.generating) delay(1)
        assertEquals(1, client.requests)

        state.analyze(this, messages(4))
        while (state.generating) delay(1)
        state.analyze(this, messages(5), automatic = true)
        assertEquals(2, client.requests)

        state.dismissSuggestion()
        state.analyze(this, messages(1), automatic = true)
        state.analyze(this, messages(3), automatic = true)
        while (state.generating) delay(1)
        assertEquals(3, client.requests)
    }

    @Test
    fun neverPersistsOpenAiApiKey() {
        var saved: AiProviderConfiguration? = null
        val state = AiSuggestionState(FakeAiSuggestionClient(), ollamaConfiguration) { saved = it }

        state.updateProvider(AiProviderKind.OpenAI)
        state.updateApiKey("secret-key")
        state.updateModel("another-model")

        assertTrue(state.isReady)
        assertEquals("", saved?.apiKey)
    }

    private fun messages(count: Int) = (1..count).map { index ->
        ChatMessage("m$index", "viewer", "message $index", StreamPlatform.Twitch)
    }

    private class FakeAiSuggestionClient : AiSuggestionClient {
        var requests = 0

        override fun testConnection(configuration: AiProviderConfiguration) = AiConnectionResult(true, "ok")

        override fun generateSuggestion(configuration: AiProviderConfiguration, messages: List<ChatMessage>): AiGeneratedSuggestion {
            requests += 1
            return AiGeneratedSuggestion("Sugestão", setOf(messages.last().id))
        }

        override fun close() = Unit
    }

    private val ollamaConfiguration = AiProviderConfiguration(
        AiProviderKind.Ollama,
        AiSuggestionState.DEFAULT_OLLAMA_ENDPOINT,
        AiSuggestionState.DEFAULT_OLLAMA_MODEL,
    )
}
