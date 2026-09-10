package dev.rocky.ui.window

import dev.rocky.core.agent.AgentConfiguration
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

    var connectionVerified by mutableStateOf(false)
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

    private var lastAnalyzedMessageId: String? = null
    private var sessionGeneration = 0L
    private var analysisJob: Job? = null

    fun updateProvider(provider: AiProviderKind) {
        connectionVerified = false
        configuration = if (provider == AiProviderKind.Ollama) {
            configuration.copy(provider = provider, endpoint = DEFAULT_OLLAMA_ENDPOINT, model = DEFAULT_OLLAMA_MODEL)
        } else {
            configuration.copy(provider = provider, endpoint = DEFAULT_OPENAI_ENDPOINT, model = DEFAULT_OPENAI_MODEL)
        }
        saveConfiguration()
    }

    fun updateEndpoint(endpoint: String) {
        connectionVerified = false
        update(configuration.copy(endpoint = endpoint))
    }

    fun updateModel(model: String) {
        connectionVerified = false
        update(configuration.copy(model = model))
    }

    fun updateApiKey(apiKey: String) {
        connectionVerified = false
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
            result.onSuccess {
                connectionVerified = it.successful
                status = it.message
            }.onFailure {
                connectionVerified = false
                status = "Não foi possível testar a conexão"
            }
        }
    }

    fun analyze(
        scope: CoroutineScope,
        messages: List<ChatMessage>,
        automatic: Boolean = false,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ) {
        if (generating || messages.isEmpty()) return
        if (!isReady) {
            status = "Configure o provedor de IA antes de analisar"
            return
        }
        if (automatic) {
            if (suggestion != null) return
            val lastIndex = lastAnalyzedMessageId?.let { id -> messages.indexOfLast { it.id == id } }
            val newMessageCount = if (lastIndex == null || lastIndex < 0) {
                messages.size
            } else {
                messages.lastIndex - lastIndex
            }
            if (!automaticAnalysis || newMessageCount < AUTOMATIC_BATCH_SIZE) return
        }
        lastAnalyzedMessageId = messages.last().id
        val snapshot = messages.takeLast(MAX_ANALYSIS_MESSAGES)
        val activeConfiguration = configuration
        val activeSession = sessionGeneration
        generating = true
        status = "Analisando ${snapshot.size} mensagens…"
        analysisJob = scope.launch {
            val result = runCatching {
                withContext(Dispatchers.Default) {
                    client.generateSuggestion(activeConfiguration, snapshot, streamerRequest, agent)
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
        lastAnalyzedMessageId = null
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
