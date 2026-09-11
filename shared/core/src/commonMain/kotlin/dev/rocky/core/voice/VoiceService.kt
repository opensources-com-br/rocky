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
    val readSuggestions: Boolean = false,
)

interface VoiceService : AutoCloseable {
    fun availableVoices(): List<SystemVoice>

    fun availableMicrophones(): List<AudioInputDevice>

    fun speak(text: String, configuration: VoiceOutputConfiguration)

    fun stopSpeaking()

    fun startCapture(microphoneId: String?)

    fun inputLevel(): Float = 0f

    fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration): String

    fun cancelCapture()

    override fun close()
}
