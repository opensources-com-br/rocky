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

    var speaking by mutableStateOf(false)
        private set

    var capturing by mutableStateOf(false)
        private set

    var transcribing by mutableStateOf(false)
        private set

    var status by mutableStateOf<String?>(null)
        private set

    var transcript by mutableStateOf<String?>(null)
        private set

    val transcriptionReady: Boolean
        get() = configuration.transcription.executablePath.isNotBlank() &&
            configuration.transcription.modelPath.isNotBlank()

    private var captureTimeout: Job? = null
    private var captureJob: Job? = null
    private var transcriptionJob: Job? = null
    private var speechJob: Job? = null
    private var captureGeneration = 0L
    private var speechGeneration = 0L
    private var lastSpokenSuggestionId: String? = null
    private var queuedSpeech: String? = null

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

    fun updateVoice(voiceId: String?) = update(
        configuration.copy(output = configuration.output.copy(voiceId = voiceId)),
    )

    fun updateSpeed(percent: Int) = update(
        configuration.copy(output = configuration.output.copy(speedPercent = percent.coerceIn(50, 150))),
    )

    fun updateVolume(percent: Int) = update(
        configuration.copy(output = configuration.output.copy(volumePercent = percent.coerceIn(0, 100))),
    )

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

    fun testVoice(scope: CoroutineScope, agentName: String = "Rocky") {
        speak(scope, "Olá, eu sou $agentName. A voz do chat, em acordes.", force = true)
    }

    fun speakSuggestion(scope: CoroutineScope, suggestionId: String, text: String, silenced: Boolean) {
        if (!configuration.readSuggestions || silenced || suggestionId == lastSpokenSuggestionId) return
        lastSpokenSuggestionId = suggestionId
        if (speaking) {
            queuedSpeech = text
            return
        }
        speak(scope, text)
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

    fun startCapture(scope: CoroutineScope, onTranscript: (String) -> Unit) {
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
                status = "Microfone ativo · clique para concluir"
                captureTimeout?.cancel()
                captureTimeout = scope.launch {
                    delay(MAX_CAPTURE_MILLIS)
                    if (capturing) stopCapture(scope, onTranscript)
                }
            }.onFailure {
                status = "Não foi possível acessar o microfone"
            }
        }
    }

    fun stopCapture(scope: CoroutineScope, onTranscript: (String) -> Unit) {
        if (!capturing || transcribing) return
        captureTimeout?.cancel()
        capturing = false
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
            }.onFailure { status = it.message ?: "Não foi possível transcrever a fala" }
        }
    }

    fun cancelCapture() {
        val wasActive = capturing || transcribing || captureJob?.isActive == true ||
            transcriptionJob?.isActive == true
        captureGeneration += 1
        captureTimeout?.cancel()
        captureTimeout = null
        captureJob?.cancel()
        captureJob = null
        transcriptionJob?.cancel()
        transcriptionJob = null
        if (wasActive) service.cancelCapture()
        capturing = false
        transcribing = false
        if (wasActive) status = "Captura cancelada"
    }

    private fun speak(scope: CoroutineScope, text: String, force: Boolean = false) {
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
                onSuccess = { "Leitura concluída" },
                onFailure = { "Não foi possível usar a voz do sistema" },
            )
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

    companion object {
        private const val MAX_CAPTURE_MILLIS = 60_000L
    }
}
