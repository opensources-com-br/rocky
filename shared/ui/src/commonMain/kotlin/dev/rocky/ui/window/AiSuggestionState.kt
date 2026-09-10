package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.RockySuggestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

internal class AiSuggestionState(
    private val client: AiSuggestionClient,
    initialConfiguration: AiProviderConfiguration,
    private val onConfigurationChange: (AiProviderConfiguration) -> Unit,
) {
    var configuration by mutableStateOf(initialConfiguration)
        private set

    var automaticAnalysis by mutableStateOf(true)
        private set

    var testing by mutableStateOf(false)
        private set

    var generating by mutableStateOf(false)
        private set

    var status by mutableStateOf<String?>(null)
        private set

    var suggestion by mutableStateOf<RockySuggestion?>(null)
        private set

    val isReady: Boolean
        get() = configuration.endpoint.isNotBlank() && configuration.model.isNotBlank() &&
            (configuration.provider != AiProviderKind.OpenAI || configuration.apiKey.isNotBlank())

    private var lastAutomaticMessageCount = 0
    private var sessionGeneration = 0L
    private var analysisJob: Job? = null

    fun updateProvider(provider: AiProviderKind) {
        configuration = if (provider == AiProviderKind.Ollama) {
            configuration.copy(provider = provider, endpoint = DEFAULT_OLLAMA_ENDPOINT, model = DEFAULT_OLLAMA_MODEL)
        } else {
            configuration.copy(provider = provider, endpoint = DEFAULT_OPENAI_ENDPOINT, model = DEFAULT_OPENAI_MODEL)
        }
        saveConfiguration()
    }

    fun updateEndpoint(endpoint: String) = update(configuration.copy(endpoint = endpoint))

    fun updateModel(model: String) = update(configuration.copy(model = model))

    fun updateApiKey(apiKey: String) {
        configuration = configuration.copy(apiKey = apiKey)
    }

    fun updateAutomaticAnalysis(enabled: Boolean) {
        automaticAnalysis = enabled
    }

    fun testConnection(scope: CoroutineScope) {
        if (testing) return
        testing = true
        status = "Testando conexão…"
        scope.launch {
            val result = runCatching {
                withContext(Dispatchers.Default) { client.testConnection(configuration) }
            }
            testing = false
            status = result.fold(
                onSuccess = { it.message },
                onFailure = { "Não foi possível testar a conexão" },
            )
        }
    }

    fun analyze(
        scope: CoroutineScope,
        messages: List<ChatMessage>,
        automatic: Boolean = false,
        streamerRequest: String? = null,
    ) {
        if (generating || messages.isEmpty()) return
        if (!isReady) {
            status = "Configure o provedor de IA antes de analisar"
            return
        }
        if (automatic) {
            if (suggestion != null) return
            if (messages.size < lastAutomaticMessageCount) lastAutomaticMessageCount = 0
            if (!automaticAnalysis || messages.size - lastAutomaticMessageCount < AUTOMATIC_BATCH_SIZE) return
        }
        lastAutomaticMessageCount = messages.size
        val snapshot = messages.takeLast(MAX_ANALYSIS_MESSAGES)
        val activeConfiguration = configuration
        val activeSession = sessionGeneration
        generating = true
        status = "Analisando ${snapshot.size} mensagens…"
        analysisJob = scope.launch {
            val result = runCatching {
                withContext(Dispatchers.Default) {
                    client.generateSuggestion(activeConfiguration, snapshot, streamerRequest)
                }
            }
            if (activeSession != sessionGeneration) return@launch
            generating = false
            result.onSuccess { generated ->
                suggestion = generated?.let {
                    RockySuggestion("ai-${Random.nextLong()}", it.text, it.sourceMessageIds)
                }
                status = if (generated == null) "Nenhuma sugestão relevante agora" else "Sugestão gerada"
            }.onFailure {
                status = "Não foi possível gerar a sugestão"
            }
        }
    }

    fun resetSession() {
        sessionGeneration += 1
        analysisJob?.cancel()
        analysisJob = null
        lastAutomaticMessageCount = 0
        generating = false
        suggestion = null
        status = null
    }

    fun dismissSuggestion() {
        suggestion = null
    }

    private fun update(value: AiProviderConfiguration) {
        configuration = value
        saveConfiguration()
    }

    private fun saveConfiguration() {
        onConfigurationChange(configuration.copy(apiKey = ""))
    }

    companion object {
        const val DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434"
        const val DEFAULT_OLLAMA_MODEL = "llama3.2"
        const val DEFAULT_OPENAI_ENDPOINT = "https://api.openai.com"
        const val DEFAULT_OPENAI_MODEL = "gpt-5.6-luna"

        private const val AUTOMATIC_BATCH_SIZE = 3
        private const val MAX_ANALYSIS_MESSAGES = 30
    }
}
