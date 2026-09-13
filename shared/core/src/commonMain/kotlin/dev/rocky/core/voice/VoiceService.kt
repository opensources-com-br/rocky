package dev.rocky.core.voice

data class SystemVoice(
    val id: String,
    val name: String,
    val language: String? = null,
)

data class AudioInputDevice(
    val id: String,
    val name: String,
)

data class VoiceOutputConfiguration(
    val voiceId: String? = null,
    val speedPercent: Int = 100,
    val volumePercent: Int = 70,
    val provider: SpeechProvider = SpeechProvider.System,
    val elevenLabs: ElevenLabsConfiguration = ElevenLabsConfiguration(),
)

data class LocalTranscriptionConfiguration(
    val executablePath: String,
    val modelPath: String,
    val microphoneId: String? = null,
    val language: String = "pt",
)

data class VoiceConfiguration(
    val output: VoiceOutputConfiguration = VoiceOutputConfiguration(),
    val transcription: LocalTranscriptionConfiguration = LocalTranscriptionConfiguration("", ""),
    val detectEndOfSpeech: Boolean = true,
    val silenceMillis: Long = 750,
    val speechThreshold: Float = 0.025f,
    val readSuggestions: Boolean = false,
)

interface VoiceService : AutoCloseable {
    fun setContinuousCapture(enabled: Boolean) = Unit
    val telemetry: VoiceTelemetry get() = VoiceTelemetry()
    fun loadVoiceCatalog(configuration: ElevenLabsConfiguration): VoiceCatalog = error("Provedor indisponível")
    val supportsInputLevel: Boolean get() = false
    val outputVolumeSupported: Boolean get() = true
    val automaticTranscriptionSetupSupported: Boolean
        get() = false

    fun availableVoices(): List<SystemVoice>

    fun availableMicrophones(): List<AudioInputDevice>

    fun speak(text: String, configuration: VoiceOutputConfiguration)

    fun stopSpeaking()

    fun startCapture(microphoneId: String?)

    fun inputLevel(): Float = 0f

    fun prepareTranscription(onProgress: (String) -> Unit): LocalTranscriptionConfiguration =
        error("Automatic voice recognition setup is unavailable")

    fun isTranscriptionConfigured(configuration: LocalTranscriptionConfiguration): Boolean =
        configuration.executablePath.isNotBlank() && configuration.modelPath.isNotBlank()

    fun detectedTranscription(): LocalTranscriptionConfiguration? = null

    fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration): String

    fun cancelCapture()

    override fun close()
}
