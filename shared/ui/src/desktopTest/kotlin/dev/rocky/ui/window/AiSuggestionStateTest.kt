package dev.rocky.ui.window

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiSuggestionStateTest {
    @Test
    fun cancelInterruptsBlockingProviderAndAllowsAnotherRequest() = runBlocking {
        val client = FakeAiSuggestionClient().apply { responseGate = CountDownLatch(1) }
        val state = AiSuggestionState(client, ollamaConfiguration) {}
        state.analyze(this, messages(1))
        withTimeout(3_000) { while (client.started.count > 0) delay(1) }
        state.cancelAnalysis()
        withTimeout(3_000) { while (!client.interrupted.get()) delay(1) }
        assertFalse(state.generating)
        assertEquals(null, state.suggestion)
        client.responseGate = null
        state.analyze(this, messages(2))
        withTimeout(3_000) { while (state.generating) delay(1) }
        assertEquals(setOf("m2"), state.suggestion?.sourceMessageIds)
    }

    @Test
    fun keepsEvidenceAfterLiveBufferChanges() = runBlocking {
        val state = AiSuggestionState(FakeAiSuggestionClient(), ollamaConfiguration) {}
        val buffer = messages(3).toMutableList()
        state.analyze(this, buffer)
        while (state.generating) delay(1)
        buffer.clear()

        val note = suggestionNote(requireNotNull(state.suggestion), state.suggestionSources, "2026-09-10 18:00")
        assertEquals(listOf("viewer: message 3"), note.evidence)
        assertEquals(setOf("m3"), note.sourceMessageIds)
        state.resetSession()
        assertTrue(state.suggestionSources.isEmpty())
    }

    @Test
    fun batchesAutomaticAnalysisAndResetsForANewSession() = runBlocking {
        val client = FakeAiSuggestionClient()
        val state = AiSuggestionState(client, ollamaConfiguration) {}
        state.updateAutomaticAnalysis(true)

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
    fun continuesAutomaticAnalysisWhenTheChatBufferIsFull() = runBlocking {
        val client = FakeAiSuggestionClient()
        val state = AiSuggestionState(client, ollamaConfiguration) {}
        state.updateAutomaticAnalysis(true)

        state.analyze(this, messages(1_000))
        while (state.generating) delay(1)
        state.dismissSuggestion()
        state.analyze(this, messages(1_003).takeLast(1_000), automatic = true)
        while (state.generating) delay(1)

        assertEquals(2, client.requests)
    }

    @Test
    fun startsWithAutomaticAnalysisDisabled() {
        val state = AiSuggestionState(FakeAiSuggestionClient(), ollamaConfiguration) {}

        assertFalse(state.automaticAnalysis)
    }

    @Test
    fun allowsTheFirstAutomaticBatchImmediatelyAndThenAppliesTheInterval() = runBlocking {
        val state = AiSuggestionState(FakeAiSuggestionClient(), ollamaConfiguration) {}
        state.updateAutomaticAnalysis(true)

        assertEquals(0L, state.automaticAnalysisDelay(1_000L, 2_000L))
        state.analyze(this, messages(3), automatic = true, automaticTimeMillis = 1_000L)
        while (state.generating) delay(1)
        assertEquals(2_000L, state.automaticAnalysisDelay(1_000L, 2_000L))

        assertEquals(0L, state.automaticAnalysisDelay(3_000L, 2_000L))
        state.resetSession()
        assertEquals(0L, state.automaticAnalysisDelay(1_000L, 2_000L))
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

    @Test
    fun configuresTheFreeOpenRouterWithoutPersistingItsKey() {
        var saved: AiProviderConfiguration? = null
        val state = AiSuggestionState(FakeAiSuggestionClient(), ollamaConfiguration) { saved = it }

        state.updateProvider(AiProviderKind.OpenRouter)
        state.updateApiKey("router-key")
        state.updateModel("openrouter/free")

        assertEquals("https://openrouter.ai/api", state.configuration.endpoint)
        assertEquals("openrouter/free", state.configuration.model)
        assertTrue(state.isReady)
        assertEquals("", saved?.apiKey)
    }

    @Test
    fun verifiesAndInvalidatesTheAiConnection() = runBlocking {
        val state = AiSuggestionState(FakeAiSuggestionClient(), ollamaConfiguration) {}

        state.testConnection(this)
        while (state.testing) delay(1)
        assertTrue(state.connectionVerified)

        state.updateModel("another-model")
        assertFalse(state.connectionVerified)
    }

    @Test
    fun forwardsStreamerSpeechToTheProvider() = runBlocking {
        val client = FakeAiSuggestionClient()
        val state = AiSuggestionState(client, ollamaConfiguration) {}

        state.analyze(this, messages(1), streamerRequest = "O que o chat achou?")
        while (state.generating) delay(1)

        assertEquals("O que o chat achou?", client.lastStreamerRequest)
    }

    @Test
    fun ignoresAResultFromAnEndedSession() = runBlocking {
        val gate = CountDownLatch(1)
        val client = FakeAiSuggestionClient().apply { responseGate = gate }
        val state = AiSuggestionState(client, ollamaConfiguration) {}

        state.analyze(this, messages(1))
        while (!state.generating) delay(1)
        state.resetSession()
        gate.countDown()
        delay(20)

        assertFalse(state.generating)
        assertEquals(null, state.suggestion)
        assertEquals(null, state.status)
    }

    private fun messages(count: Int) = (1..count).map { index ->
        ChatMessage("m$index", "viewer", "message $index", StreamPlatform.Twitch)
    }

    private class FakeAiSuggestionClient : AiSuggestionClient {
        val started = CountDownLatch(1)
        val interrupted = AtomicBoolean(false)
        var requests = 0
        var lastStreamerRequest: String? = null
        var responseGate: CountDownLatch? = null

        override fun testConnection(configuration: AiProviderConfiguration) = AiConnectionResult(true, "ok")

        override fun generateSuggestion(
            configuration: AiProviderConfiguration,
            messages: List<ChatMessage>,
            streamerRequest: String?,
            agent: AgentConfiguration,
        ): AiGeneratedSuggestion {
            requests += 1
            lastStreamerRequest = streamerRequest
            started.countDown()
            try {
                check(responseGate?.await(3, TimeUnit.SECONDS) != false)
            } catch (error: InterruptedException) {
                interrupted.set(true)
                throw error
            }
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
