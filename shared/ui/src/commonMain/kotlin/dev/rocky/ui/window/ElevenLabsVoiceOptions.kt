package dev.rocky.ui.window

import androidx.compose.material.*
import androidx.compose.runtime.Composable

@Composable
internal fun ElevenLabsVoiceOptions(voice: VoiceState) {
    val output = voice.configuration.output
    val settings = output.elevenLabs
    val catalog = voice.voiceCatalog
    if (catalog != null) {
        SpeechChoice(tr("Voice", "Voz"), settings.voiceId, catalog.voices.map { it.id to it.name }) {
            voice.updateOutput(output.copy(elevenLabs = settings.copy(voiceId = it)))
        }
        SpeechChoice(tr("Model", "Modelo"), settings.modelId, catalog.models.map { it.id to it.name }) {
            voice.updateOutput(output.copy(elevenLabs = settings.copy(modelId = it)))
        }
    }
    OutlinedTextField(settings.voiceId, { voice.updateOutput(output.copy(elevenLabs = settings.copy(voiceId = it.trim()))) },
        label = { Text("Voice ID") }, singleLine = true)
    OutlinedTextField(settings.modelId, { voice.updateOutput(output.copy(elevenLabs = settings.copy(modelId = it.trim()))) },
        label = { Text("Model ID") }, singleLine = true)
    SettingSwitch(tr("Use system voice if ElevenLabs fails before playback", "Usar voz local se ElevenLabs falhar antes de reproduzir"), settings.fallbackToSystem) {
        voice.updateOutput(output.copy(elevenLabs = settings.copy(fallbackToSystem = it)))
    }
}
