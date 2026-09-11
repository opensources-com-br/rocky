package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.rocky.core.voice.AudioInputDevice
import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.SystemVoice
import dev.rocky.core.voice.VoiceConfiguration
import dev.rocky.core.voice.VoiceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class VoiceState(
    private val service: VoiceService,
    initialConfiguration: VoiceConfiguration,
    private val captureDurationMillis: Long = 8_000L,
    private val onConfigurationChange: (VoiceConfiguration) -> Unit,
) {
    var configuration by mutableStateOf(initialConfiguration)
        private set

    var voices by mutableStateOf<List<SystemVoice>>(emptyList())
        private set

    var microphones by mutableStateOf<List<AudioInputDevice>>(emptyList())
        private set

    var loadingDevices by mutableStateOf(false)
        private set

    var preparingTranscription by mutableStateOf(false)
        private set

    var transcriptionSetupStatus by mutableStateOf<String?>(null)
        private set

    var speaking by mutableStateOf(false)
        private set

    var voiceTested by mutableStateOf(false)
        private set

    var capturing by mutableStateOf(false)
        private set

    var transcribing by mutableStateOf(false)
        private set

    var listenerEnabled by mutableStateOf(false)
        private set

    var inputLevel by mutableStateOf(0f)
        private set

    var status by mutableStateOf<String?>(null)
        private set

    var transcript by mutableStateOf<String?>(null)
        private set

    var conversationTestTranscript by mutableStateOf<String?>(null)
        private set

    var conversationTestResponse by mutableStateOf<String?>(null)
        private set

    var conversationTesting by mutableStateOf(false)
        private set

    val transcriptionReady: Boolean
        get() = configuration.transcription.executablePath.isNotBlank() &&
            configuration.transcription.modelPath.isNotBlank()

    val automaticTranscriptionSetupSupported: Boolean
        get() = service.automaticTranscriptionSetupSupported

    private var captureTimeout: Job? = null
    private var captureJob: Job? = null
    private var transcriptionJob: Job? = null
    private var levelJob: Job? = null
    private var speechJob: Job? = null
    private var captureGeneration = 0L
    private var speechGeneration = 0L
    private var lastSpokenSuggestionId: String? = null
    private var queuedSpeech: String? = null
    private var listenerScope: CoroutineScope? = null
    private var listenerTranscript: ((String) -> Unit)? = null

    fun toggleListener(scope: CoroutineScope, onTranscript: (String) -> Unit) {
        if (listenerEnabled) disableListener() else enableListener(scope, onTranscript)
    }

    fun enableListener(scope: CoroutineScope, onTranscript: (String) -> Unit) {
        if (listenerEnabled) return
        if (!transcriptionReady) {
            status = "Configure o whisper.cpp na aba Voz antes de usar o microfone"
            return
        }
        listenerEnabled = true
        listenerScope = scope
        listenerTranscript = onTranscript
        startCapture(scope, onTranscript = onTranscript)
    }

    fun disableListener() {
        if (!listenerEnabled) return
        listenerEnabled = false
        cancelCapture()
        status = "Ouvinte desativado"
    }

    fun resumeListener() {
        if (!listenerEnabled || capturing || transcribing || speaking) return
        val scope = listenerScope ?: return
        val onTranscript = listenerTranscript ?: return
        startCapture(scope, onTranscript = onTranscript)
    }

    fun loadDevices(scope: CoroutineScope) {
        if (loadingDevices || voices.isNotEmpty() || microphones.isNotEmpty()) return
        loadingDevices = true
        scope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching { service.availableVoices() to service.availableMicrophones() }
            }
            loadingDevices = false
            result.onSuccess {
                voices = it.first
                microphones = it.second
            }.onFailure { status = "Não foi possível consultar os dispositivos de áudio" }
        }
    }

    fun updateVoice(voiceId: String?) {
        voiceTested = false
        update(configuration.copy(output = configuration.output.copy(voiceId = voiceId)))
    }

    fun updateSpeed(percent: Int) {
        voiceTested = false
        update(configuration.copy(output = configuration.output.copy(speedPercent = percent.coerceIn(50, 150))))
    }

    fun updateVolume(percent: Int) {
        voiceTested = false
        update(configuration.copy(output = configuration.output.copy(volumePercent = percent.coerceIn(0, 100))))
    }

    fun updateReadSuggestions(enabled: Boolean) {
        if (!enabled) stopSpeaking()
        update(configuration.copy(readSuggestions = enabled))
    }

    fun updateWhisperExecutable(path: String) = update(
        configuration.copy(transcription = configuration.transcription.copy(executablePath = path)),
    )

    fun updateWhisperModel(path: String) = update(
        configuration.copy(transcription = configuration.transcription.copy(modelPath = path)),
    )

    fun updateMicrophone(id: String?) = update(
        configuration.copy(transcription = configuration.transcription.copy(microphoneId = id)),
    )

    fun prepareTranscription(scope: CoroutineScope) {
        if (preparingTranscription || !automaticTranscriptionSetupSupported) return
        preparingTranscription = true
        transcriptionSetupStatus = "Preparando reconhecimento de voz…"
        scope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching { service.prepareTranscription { transcriptionSetupStatus = it } }
            }
            preparingTranscription = false
            result.onSuccess { prepared ->
                update(
                    configuration.copy(
                        transcription = prepared.copy(
                            microphoneId = configuration.transcription.microphoneId,
                            language = configuration.transcription.language,
                        ),
                    ),
                )
                transcriptionSetupStatus = "Reconhecimento de voz pronto"
                status = "Faça o teste de conversa abaixo"
            }.onFailure {
                transcriptionSetupStatus = it.message ?: "Não foi possível preparar o reconhecimento de voz"
            }
        }
    }

    fun testVoice(scope: CoroutineScope, agentName: String = "Rocky") {
        voiceTested = false
        speak(
            scope,
            "Olá, eu sou $agentName. A voz do chat, em acordes.",
            force = true,
            onSuccess = { voiceTested = true },
        )
    }

    fun testConversation(scope: CoroutineScope, agentName: String = "Rocky") {
        if (conversationTesting) return
        if (!transcriptionReady) {
            status = "Configure o reconhecimento de voz para iniciar o teste"
            return
        }
        cancelCapture()
        conversationTesting = true
        conversationTestTranscript = null
        conversationTestResponse = null
        startCapture(
            scope,
            onTranscript = { text ->
                conversationTestTranscript = text
                val response = "Eu ouvi você dizer: $text. Meu microfone está funcionando."
                conversationTestResponse = response
                speakAcknowledgement(scope, response, silenced = false) {
                    conversationTesting = false
                    resumeListener()
                }
            },
            onFailure = { conversationTesting = false },
        )
    }

    fun speakSuggestion(
        scope: CoroutineScope,
        suggestionId: String,
        text: String,
        silenced: Boolean,
        force: Boolean = false,
        onFinished: () -> Unit = {},
    ) {
        if (silenced || suggestionId == lastSpokenSuggestionId) {
            onFinished()
            return
        }
        if (!force && !configuration.readSuggestions) return
        lastSpokenSuggestionId = suggestionId
        if (speaking) {
            queuedSpeech = text
            return
        }
        speak(scope, text, force = force, onFinished = onFinished)
    }

    fun speakAcknowledgement(
        scope: CoroutineScope,
        text: String,
        silenced: Boolean,
        onFinished: () -> Unit,
    ) {
        if (silenced) {
            onFinished()
            return
        }
        speak(scope, text, force = true, onFinished = onFinished)
    }

    fun stopSpeaking() {
        val wasSpeaking = speaking || speechJob?.isActive == true
        speechGeneration += 1
        speechJob?.cancel()
        speechJob = null
        queuedSpeech = null
        service.stopSpeaking()
        speaking = false
        if (wasSpeaking) status = "Leitura interrompida"
    }

    fun startCapture(
        scope: CoroutineScope,
        onFailure: () -> Unit = {},
        onTranscript: (String) -> Unit,
    ) {
        if (capturing || transcribing || captureJob?.isActive == true) return
        if (!transcriptionReady) {
            status = "Configure o whisper.cpp na aba Voz antes de usar o microfone"
            return
        }
        stopSpeaking()
        transcript = null
        status = "Ativando o microfone…"
        val generation = ++captureGeneration
        captureJob = scope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching { service.startCapture(configuration.transcription.microphoneId) }
            }
            if (generation != captureGeneration) {
                if (result.isSuccess) service.cancelCapture()
                return@launch
            }
            captureJob = null
            result.onSuccess {
                capturing = true
                status = "Ouvinte ativo · diga “Rocky” e faça sua pergunta"
                levelJob?.cancel()
                levelJob = scope.launch {
                    while (capturing) {
                        inputLevel = service.inputLevel()
                        delay(INPUT_LEVEL_REFRESH_MILLIS)
                    }
                    inputLevel = 0f
                }
                captureTimeout?.cancel()
                captureTimeout = scope.launch {
                    delay(captureDurationMillis)
                    if (capturing) stopCapture(scope, onFailure, onTranscript)
                }
            }.onFailure {
                status = "Não foi possível acessar o microfone"
                onFailure()
            }
        }
    }

    fun stopCapture(
        scope: CoroutineScope,
        onFailure: () -> Unit = {},
        onTranscript: (String) -> Unit,
    ) {
        if (!capturing || transcribing) return
        captureTimeout?.cancel()
        capturing = false
        levelJob?.cancel()
        levelJob = null
        inputLevel = 0f
        transcribing = true
        status = "Transcrevendo localmente…"
        val activeConfiguration: LocalTranscriptionConfiguration = configuration.transcription
        val generation = ++captureGeneration
        transcriptionJob = scope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching { service.stopCaptureAndTranscribe(activeConfiguration) }
            }
            if (generation != captureGeneration) return@launch
            transcriptionJob = null
            transcribing = false
            result.onSuccess { text ->
                transcript = text
                status = "Você: $text"
                onTranscript(text)
            }.onFailure {
                status = it.message ?: "Não foi possível transcrever a fala"
                onFailure()
                resumeListener()
            }
        }
    }

    fun cancelCapture() {
        val wasActive = capturing || transcribing || captureJob?.isActive == true ||
            transcriptionJob?.isActive == true
        captureGeneration += 1
        captureTimeout?.cancel()
        captureTimeout = null
        levelJob?.cancel()
        levelJob = null
        captureJob?.cancel()
        captureJob = null
        transcriptionJob?.cancel()
        transcriptionJob = null
        if (wasActive) service.cancelCapture()
        capturing = false
        transcribing = false
        inputLevel = 0f
        if (wasActive) status = "Captura cancelada"
    }

    fun resetSession() {
        listenerEnabled = false
        listenerScope = null
        listenerTranscript = null
        stopSpeaking()
        cancelCapture()
        transcript = null
        conversationTesting = false
        conversationTestTranscript = null
        conversationTestResponse = null
        status = null
        lastSpokenSuggestionId = null
    }

    private fun speak(
        scope: CoroutineScope,
        text: String,
        force: Boolean = false,
        onSuccess: () -> Unit = {},
        onFinished: () -> Unit = {},
    ) {
        if (speaking || (!force && !configuration.readSuggestions)) return
        val generation = ++speechGeneration
        speaking = true
        status = "Rocky está falando…"
        val output = configuration.output
        speechJob = scope.launch {
            val result = withContext(Dispatchers.Default) { runCatching { service.speak(text, output) } }
            if (generation != speechGeneration) return@launch
            speechJob = null
            speaking = false
            status = result.fold(
                onSuccess = {
                    onSuccess()
                    "Leitura concluída"
                },
                onFailure = { "Não foi possível usar a voz do sistema" },
            )
            onFinished()
            if (result.isSuccess) {
                queuedSpeech?.let { next ->
                    queuedSpeech = null
                    speak(scope, next)
                }
            }
        }
    }

    private fun update(value: VoiceConfiguration) {
        configuration = value
        onConfigurationChange(value)
    }

    private companion object {
        const val INPUT_LEVEL_REFRESH_MILLIS = 75L
    }

}
