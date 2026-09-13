package dev.rocky.core.voice

interface AudioCapture {
    fun start(microphoneId: String?)
    fun level(): Float
    fun finish(): ByteArray
    fun cancel()
}

interface SpeechTranscriber {
    fun transcribe(audio: ByteArray, configuration: LocalTranscriptionConfiguration): String
    fun cancel()
}

interface SpeechOutput {
    fun speak(text: String, configuration: VoiceOutputConfiguration)
    fun cancel()
}

interface PcmPlayback {
    fun start(sampleRate: Int, volumePercent: Int)
    fun write(bytes: ByteArray, count: Int)
    fun finish()
    fun cancel()
}
