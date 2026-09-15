package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.rocky.core.voice.*
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun VoiceProviderSettings(voice: VoiceState) {
    val output = voice.configuration.output
    val settings = output.elevenLabs
    val scope = rememberCoroutineScope()
    SettingsPreferenceGroup(tr("Provider and connection", "Provedor e conexão")) {
        SpeechChoice(tr("Speech provider", "Provedor de voz"), output.provider.name,
            listOf("System" to tr("System voice", "Voz do sistema"), "ElevenLabs" to "ElevenLabs"), "voice-provider-menu") {
            voice.updateOutput(output.copy(provider = SpeechProvider.valueOf(it)))
        }
        if (output.provider != SpeechProvider.ElevenLabs) return@SettingsPreferenceGroup
        Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(tr("Speech text is sent to ElevenLabs. Voice tests may consume your account credits.",
                "O texto da resposta é enviado à ElevenLabs. Testes de voz podem consumir créditos da sua conta."),
                color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
            var key by remember(settings.apiKey) { mutableStateOf(settings.apiKey) }
            OutlinedTextField(key, { key = it }, modifier = Modifier.fillMaxWidth().testTag("elevenlabs-api-key"),
                label = { Text("ElevenLabs API key") }, singleLine = true,
                shape = RoundedCornerShape(8.dp), textStyle = MaterialTheme.typography.body2,
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = RockyColors.Surface,
                    textColor = RockyColors.TextPrimary, unfocusedBorderColor = RockyColors.Border, focusedBorderColor = RockyColors.Accent),
                visualTransformation = PasswordVisualTransformation())
            OutlinedButton(onClick = { voice.updateOutput(output.copy(elevenLabs = settings.copy(apiKey = key.trim()))) }) {
                Text(tr("Save key", "Salvar chave"))
            }
            OutlinedButton(enabled = !voice.loadingCatalog && settings.apiKey.isNotBlank(), onClick = { voice.loadCatalog(scope) }) {
                Text(if (voice.loadingCatalog) tr("Loading…", "Carregando…") else tr("Load voices and models", "Carregar vozes e modelos"))
            }
        }
        ElevenLabsVoiceOptions(voice)
    }
}
