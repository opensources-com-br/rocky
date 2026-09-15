package dev.rocky.ui.window

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

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
    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val fieldColors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = RockyColors.Surface,
            textColor = RockyColors.TextPrimary, unfocusedBorderColor = RockyColors.Border,
            focusedBorderColor = RockyColors.Accent)
        OutlinedTextField(settings.voiceId, { voice.updateOutput(output.copy(elevenLabs = settings.copy(voiceId = it.trim()))) },
            modifier = Modifier.fillMaxWidth().testTag("elevenlabs-voice-id"), label = { Text("Voice ID") },
            singleLine = true, shape = RoundedCornerShape(8.dp), textStyle = MaterialTheme.typography.body2, colors = fieldColors)
        OutlinedTextField(settings.modelId, { voice.updateOutput(output.copy(elevenLabs = settings.copy(modelId = it.trim()))) },
            modifier = Modifier.fillMaxWidth().testTag("elevenlabs-model-id"), label = { Text("Model ID") },
            singleLine = true, shape = RoundedCornerShape(8.dp), textStyle = MaterialTheme.typography.body2, colors = fieldColors)
    }
    SettingsPreferenceRow(tr("Local fallback", "Alternativa local"),
        tr("Use the system voice if ElevenLabs fails before playback.", "Usar voz do sistema se a ElevenLabs falhar antes de reproduzir."),
        stackWhenCompact = true) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Switch(settings.fallbackToSystem, {
                voice.updateOutput(output.copy(elevenLabs = settings.copy(fallbackToSystem = it)))
            }, modifier = Modifier.testTag("elevenlabs-fallback"), colors = SwitchDefaults.colors(
                checkedThumbColor = RockyColors.TextPrimary, checkedTrackColor = RockyColors.Accent))
        }
    }
}
