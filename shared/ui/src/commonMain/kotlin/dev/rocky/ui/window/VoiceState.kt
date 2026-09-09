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
    private var lastSpokenSuggestionId: String? = null

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

    fun updateReadSuggestions(enabled: Boolean) = update(configuration.copy(readSuggestions = enabled))

    fun updateWhisperExecutable(path: String) = update(
        configuration.copy(transcription = configuration.transcription.copy(executablePath = path)),
    )

    fun updateWhisperModel(path: String) = update(
        configuration.copy(transcription = configuration.transcription.copy(modelPath = path)),
    )

    fun updateMicrophone(id: String?) = update(
        configuration.copy(transcription = configuration.transcription.copy(microphoneId = id)),
    )

    fun testVoice(scope: CoroutineScope) {
        speak(scope, "Olá, eu sou o Rocky. A voz do chat, em acordes.", force = true)
    }

    fun speakSuggestion(scope: CoroutineScope, suggestionId: String, text: String, silenced: Boolean) {
        if (!configuration.readSuggestions || silenced || suggestionId == lastSpokenSuggestionId) return
        lastSpokenSuggestionId = suggestionId
        speak(scope, text)
    }

    fun stopSpeaking() {
        if (!speaking) return
        service.stopSpeaking()
        speaking = false
        status = "Leitura interrompida"
    }

    fun startCapture(scope: CoroutineScope, onTranscript: (String) -> Unit) {
        if (capturing || transcribing) return
        if (!transcriptionReady) {
            status = "Configure o whisper.cpp na aba Voz antes de usar o microfone"
            return
        }
        transcript = null
        capturing = true
        status = "Ativando o microfone…"
        scope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching { service.startCapture(configuration.transcription.microphoneId) }
            }
            result.onSuccess {
                status = "Microfone ativo · clique para concluir"
                captureTimeout?.cancel()
                captureTimeout = scope.launch {
                    delay(MAX_CAPTURE_MILLIS)
                    if (capturing) stopCapture(scope, onTranscript)
                }
            }.onFailure {
                capturing = false
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
        scope.launch {
            val result = withContext(Dispatchers.Default) {
                runCatching { service.stopCaptureAndTranscribe(activeConfiguration) }
            }
            transcribing = false
            result.onSuccess { text ->
                transcript = text
                status = "Você: $text"
                onTranscript(text)
            }.onFailure { status = it.message ?: "Não foi possível transcrever a fala" }
        }
    }

    fun cancelCapture() {
        captureTimeout?.cancel()
        if (capturing) service.cancelCapture()
        capturing = false
        transcribing = false
    }

    private fun speak(scope: CoroutineScope, text: String, force: Boolean = false) {
        if (speaking || (!force && !configuration.readSuggestions)) return
        speaking = true
        status = "Rocky está falando…"
        val output = configuration.output
        scope.launch {
            val result = withContext(Dispatchers.Default) { runCatching { service.speak(text, output) } }
            speaking = false
            status = result.fold(
                onSuccess = { "Leitura concluída" },
                onFailure = { "Não foi possível usar a voz do sistema" },
            )
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
