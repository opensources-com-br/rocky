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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random

internal data class ConversationEntry(
    val question: String,
    val answer: RockySuggestion,
    val sources: List<ChatMessage>,
)

internal class AiSuggestionState(
    private val client: AiSuggestionClient,
    initialConfiguration: AiProviderConfiguration,
    initialAutomaticAnalysis: Boolean = false,
    private val onAutomaticAnalysisChange: (Boolean) -> Unit = {},
    initialProfile: dev.rocky.core.agent.InterventionProfile? = null,
    private val onProfileChange: (dev.rocky.core.agent.InterventionProfile) -> Unit = {},
    initialFilters: dev.rocky.core.live.ChatFilterConfiguration = dev.rocky.core.live.ChatFilterConfiguration(),
    private val onFiltersChange: (dev.rocky.core.live.ChatFilterConfiguration) -> Unit = {},
    private val onConfigurationChange: (AiProviderConfiguration) -> Unit,
) {
    var filters by mutableStateOf(initialFilters); private set
    var filteredCount by mutableStateOf(0); private set
    var profile by mutableStateOf(initialProfile ?: if (initialAutomaticAnalysis)
        dev.rocky.core.agent.InterventionProfile.Discreet else dev.rocky.core.agent.InterventionProfile.OnDemand)
        private set
    fun updateFilters(value: dev.rocky.core.live.ChatFilterConfiguration) { filters = value; onFiltersChange(value) }
    fun updateProfile(value: dev.rocky.core.agent.InterventionProfile) {
        if (generating && activeAutomatic) cancelAnalysis()
        profile = value
        automaticAnalysis = value != dev.rocky.core.agent.InterventionProfile.OnDemand
        onAutomaticAnalysisChange(automaticAnalysis); onProfileChange(value)
    }

    var suggestionSources by mutableStateOf<List<ChatMessage>>(emptyList())
        private set

    var configuration by mutableStateOf(initialConfiguration)
        private set

    var automaticAnalysis by mutableStateOf(profile != dev.rocky.core.agent.InterventionProfile.OnDemand)
        private set

    var models by mutableStateOf<List<String>>(emptyList())
        private set
    var loadingModels by mutableStateOf(false)
        private set

    fun loadModels(scope: CoroutineScope) {
        if (loadingModels) return
        val requested = configuration
        loadingModels = true
        scope.launch {
            try {
                val result = runCatching { interruptibleWork { client.availableModels(requested) } }
                if (configuration != requested) return@launch
                result.onSuccess {
                    models = it
                    status = if (it.isEmpty()) "Nenhum modelo disponível. Verifique o provedor." else "Modelos carregados"
                }.onFailure {
                    if (it is CancellationException) throw it
                    status = "Não foi possível listar modelos. Verifique a conexão e a chave."
                }
            } finally { loadingModels = false }
        }
    }

    var testing by mutableStateOf(false)
        private set

    var connectionVerified by mutableStateOf(false)
        private set

    var generating by mutableStateOf(false)
        private set

    var analysisRevision by mutableStateOf(0)
        private set

    var lastDurationMillis by mutableStateOf<Long?>(null)
        private set
    var reportedTokens by mutableStateOf(0L)
        private set
    var completedRequests by mutableStateOf(0)
        private set
    var lastAttempts by mutableStateOf(1)
        private set

    var status by mutableStateOf<String?>(null)
        private set

    var suggestion by mutableStateOf<RockySuggestion?>(null)
        private set

    val isReady: Boolean
        get() = configuration.endpoint.isNotBlank() && configuration.model.isNotBlank() &&
            (configuration.provider == AiProviderKind.Ollama || configuration.apiKey.isNotBlank())

    private var lastAnalyzedMessageId: String? = null
    private var lastAutomaticAnalysisAtMillis: Long? = null
    private var sessionGeneration = 0L
    private var analysisJob: Job? = null
    private var activeAutomatic = false
    val acceptsDirectRequest: Boolean get() = !generating || activeAutomatic
    val history = androidx.compose.runtime.mutableStateListOf<ConversationEntry>()

    fun updateProvider(provider: AiProviderKind) {
        if (provider == configuration.provider) return
        cancelAnalysis()
        models = emptyList()
        connectionVerified = false
        configuration = configuration.copy(apiKey = "")
        configuration = when (provider) {
            AiProviderKind.Ollama -> configuration.copy(
                provider = provider, endpoint = DEFAULT_OLLAMA_ENDPOINT, model = DEFAULT_OLLAMA_MODEL,
            )
            AiProviderKind.OpenAI -> configuration.copy(
                provider = provider, endpoint = DEFAULT_OPENAI_ENDPOINT, model = DEFAULT_OPENAI_MODEL,
            )
            AiProviderKind.OpenRouter -> configuration.copy(
                provider = provider, endpoint = DEFAULT_OPENROUTER_ENDPOINT, model = DEFAULT_OPENROUTER_MODEL,
            )
        }
        saveConfiguration()
    }

    fun updateEndpoint(endpoint: String) {
        connectionVerified = false
        if (endpoint.trim().trimEnd('/') != configuration.endpoint.trim().trimEnd('/')) {
            cancelAnalysis()
            models = emptyList()
            configuration = configuration.copy(endpoint = endpoint, apiKey = "")
            saveConfiguration()
        } else update(configuration.copy(endpoint = endpoint))
    }

    fun updateModel(model: String) {
        connectionVerified = false
        update(configuration.copy(model = model))
    }

    fun updateApiKey(apiKey: String) {
        if (generating && apiKey != configuration.apiKey) cancelAnalysis()
        connectionVerified = false
        configuration = configuration.copy(apiKey = apiKey)
    }

    fun updateAutomaticAnalysis(enabled: Boolean) {
        updateProfile(if (enabled) dev.rocky.core.agent.InterventionProfile.Discreet else dev.rocky.core.agent.InterventionProfile.OnDemand)
    }

    fun automaticAnalysisDelay(nowMillis: Long, intervalMillis: Long): Long = lastAutomaticAnalysisAtMillis
        ?.let { lastAnalysis -> (lastAnalysis + intervalMillis - nowMillis).coerceAtLeast(0L) }
        ?: 0L

    fun testConnection(scope: CoroutineScope) {
        if (testing) return
        testing = true
        val testedConfiguration = configuration
        status = "Testando conexão…"
        scope.launch {
            val result = runCatching {
                interruptibleWork { client.testConnection(testedConfiguration) }
            }
            testing = false
            if (configuration != testedConfiguration) return@launch
            result.exceptionOrNull()?.let { if (it is CancellationException) throw it }
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
        automaticTimeMillis: Long = 0L,
        messageLimit: Int = MAX_ANALYSIS_MESSAGES,
        onComplete: (RockySuggestion?) -> Unit = {},
    ) {
        if (generating) {
            if (!automatic && activeAutomatic) cancelAnalysis()
            else { onComplete(null); return }
        }
        if (messages.isEmpty() && streamerRequest.isNullOrBlank()) {
            status = "Nenhuma mensagem recebida nos últimos dois minutos"
            onComplete(null)
            return
        }
        if (!isReady) {
            status = "Configure o provedor de IA antes de analisar"
            onComplete(null)
            return
        }
        if (automatic) {
            val lastIndex = lastAnalyzedMessageId?.let { id -> messages.indexOfLast { it.id == id } }
            val newMessageCount = if (lastIndex == null || lastIndex < 0) {
                messages.size
            } else {
                messages.lastIndex - lastIndex
            }
            if (!automaticAnalysis || newMessageCount < AUTOMATIC_BATCH_SIZE) return
            lastAutomaticAnalysisAtMillis = automaticTimeMillis
        }
        if (!automatic) lastAnalyzedMessageId = messages.lastOrNull()?.id
        val snapshot = messages.takeLast(messageLimit.coerceIn(1, MAX_ANALYSIS_MESSAGES))
        val conversationAgent = agent.copy(conversation = if (automatic) emptyList() else history.takeLast(4).map {
            dev.rocky.core.agent.ConversationTurn(it.question, it.answer.text)
        })
        val activeConfiguration = configuration
        activeAutomatic = automatic
        val activeSession = sessionGeneration
        suggestion = null
        suggestionSources = emptyList()
        generating = true
        status = "Analisando ${snapshot.size} mensagens…"
        analysisJob = scope.launch {
            val started = kotlin.time.TimeSource.Monotonic.markNow()
            val result = runCatching {
                interruptibleWork {
                    client.generateSuggestion(activeConfiguration, snapshot, streamerRequest, conversationAgent)
                }
            }
            if (activeSession != sessionGeneration) return@launch
            generating = false
            lastDurationMillis = started.elapsedNow().inWholeMilliseconds
            completedRequests += 1
            analysisRevision += 1
            result.exceptionOrNull()?.let { if (it is CancellationException) throw it }
            result.onSuccess { generated ->
                reportedTokens += generated?.reportedTokens ?: 0L
                lastAttempts = generated?.attempts ?: 1
                if (automatic) lastAnalyzedMessageId = snapshot.last().id
                suggestionSources = snapshot.filter { it.id in generated?.sourceMessageIds.orEmpty() }
                val completedSuggestion = generated?.let {
                    RockySuggestion("ai-${Random.nextLong()}", it.text, it.sourceMessageIds)
                }
                if (completedSuggestion != null) {
                    history.add(ConversationEntry(streamerRequest ?: "Análise do chat", completedSuggestion, suggestionSources.toList()))
                    if (history.size > 30) history.removeAt(0)
                }
                suggestion = completedSuggestion
                status = if (generated == null) "Nenhuma sugestão relevante agora" else "Sugestão gerada"
                onComplete(completedSuggestion)
            }.onFailure {
                status = (it as? dev.rocky.core.ai.AiRequestException)?.message ?: "Não foi possível gerar a sugestão"
                onComplete(null)
            }
        }
    }

    fun resetSession() {
        history.clear()
        sessionGeneration += 1
        analysisJob?.cancel()
        analysisJob = null
        lastDurationMillis = null
        reportedTokens = 0
        completedRequests = 0
        lastAnalyzedMessageId = null
        lastAutomaticAnalysisAtMillis = null
        generating = false
        suggestion = null
        suggestionSources = emptyList()
        status = null
    }

    fun cancelAnalysis() {
        sessionGeneration += 1
        analysisJob?.cancel()
        analysisJob = null
        generating = false
        status = "Análise cancelada"
    }

    fun dismissSuggestion() {
        suggestion = null
        suggestionSources = emptyList()
    }

    private fun update(value: AiProviderConfiguration) {
        configuration = value
    }

    fun showNotice(value: String) { status = value }

    fun saveConfiguration() {
        runCatching { onConfigurationChange(configuration) }
            .onSuccess { status = "Configuração salva no dispositivo" }
            .onFailure { status = "Não foi possível salvar no cofre. Desbloqueie o cofre e tente novamente." }
    }

    companion object {
        const val DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434"
        const val DEFAULT_OLLAMA_MODEL = "llama3.2"
        const val DEFAULT_OPENAI_ENDPOINT = "https://api.openai.com"
        const val DEFAULT_OPENAI_MODEL = ""
        const val DEFAULT_OPENROUTER_ENDPOINT = "https://openrouter.ai/api"
        const val DEFAULT_OPENROUTER_MODEL = "openrouter/free"

        private const val AUTOMATIC_BATCH_SIZE = 3
        private const val MAX_ANALYSIS_MESSAGES = 200
    }
}
