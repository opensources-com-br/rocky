package dev.rocky.data.voice

import dev.rocky.core.voice.*

internal class SpeechFixture : VoiceService, PcmPlayback {
    var spoken = mutableListOf<String>()
    var captured = false
    var samples = 0
    var finished = false
    var failWrite = false
    override fun availableVoices() = emptyList<SystemVoice>()
    override fun availableMicrophones() = emptyList<AudioInputDevice>()
    override fun speak(text: String, configuration: VoiceOutputConfiguration) { spoken += text }
    override fun stopSpeaking() {}
    override fun startCapture(microphoneId: String?) { captured = true }
    override fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration) = ""
    override fun cancelCapture() { captured = false }
    override fun close() {}
    override fun start(sampleRate: Int, volumePercent: Int) { check(sampleRate == 24_000) }
    override fun write(bytes: ByteArray, count: Int) {
        check(!captured) { "Microphone must be paused during playback" }
        if (failWrite) error("Playback failed")
        samples += count / 2
    }
    override fun finish() { finished = true }
    override fun cancel() {}
}
