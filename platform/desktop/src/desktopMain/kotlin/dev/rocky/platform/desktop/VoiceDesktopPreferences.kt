package dev.rocky.platform.desktop

import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.VoiceConfiguration
import dev.rocky.core.voice.VoiceOutputConfiguration
import java.util.prefs.Preferences

object VoiceDesktopPreferences {
    private val preferences = Preferences.userRoot().node("dev/rocky/voice")

    var configuration: VoiceConfiguration
        get() = VoiceConfiguration(
            output = VoiceOutputConfiguration(
                voiceId = preferences.get(VOICE_KEY, "").ifBlank { null },
                speedPercent = preferences.getInt(SPEED_KEY, 100),
                volumePercent = preferences.getInt(VOLUME_KEY, 70),
            ),
            transcription = LocalTranscriptionConfiguration(
                executablePath = preferences.get(WHISPER_EXECUTABLE_KEY, ""),
                modelPath = preferences.get(WHISPER_MODEL_KEY, ""),
                microphoneId = preferences.get(MICROPHONE_KEY, "").ifBlank { null },
            ),
            detectEndOfSpeech = preferences.getBoolean("detectEndOfSpeech", true),
            silenceMillis = preferences.getLong("silenceMillis", 750).coerceIn(450, 1500),
            speechThreshold = preferences.getFloat("speechThreshold", 0.025f).coerceIn(0.01f, 0.15f),
            readSuggestions = preferences.getBoolean(READ_SUGGESTIONS_KEY, false),
        )
        set(value) {
            preferences.putBoolean("detectEndOfSpeech", value.detectEndOfSpeech)
            preferences.putLong("silenceMillis", value.silenceMillis)
            preferences.putFloat("speechThreshold", value.speechThreshold)
            preferences.put(VOICE_KEY, value.output.voiceId.orEmpty())
            preferences.putInt(SPEED_KEY, value.output.speedPercent)
            preferences.putInt(VOLUME_KEY, value.output.volumePercent)
            preferences.put(WHISPER_EXECUTABLE_KEY, value.transcription.executablePath.trim())
            preferences.put(WHISPER_MODEL_KEY, value.transcription.modelPath.trim())
            preferences.put(MICROPHONE_KEY, value.transcription.microphoneId.orEmpty())
            preferences.putBoolean(READ_SUGGESTIONS_KEY, value.readSuggestions)
        }

    private const val VOICE_KEY = "voice"
    private const val SPEED_KEY = "speed"
    private const val VOLUME_KEY = "volume"
    private const val WHISPER_EXECUTABLE_KEY = "whisperExecutable"
    private const val WHISPER_MODEL_KEY = "whisperModel"
    private const val MICROPHONE_KEY = "microphone"
    private const val READ_SUGGESTIONS_KEY = "readSuggestions"
}
