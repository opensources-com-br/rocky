package dev.rocky.ui.window

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import dev.rocky.core.voice.*

@Composable
internal fun VoiceProviderSettings(voice: VoiceState) {
    val output = voice.configuration.output
    val settings = output.elevenLabs
    val scope = rememberCoroutineScope()
    SpeechChoice(tr("Speech provider", "Provedor de voz"), output.provider.name,
        listOf("System" to tr("System voice", "Voz do sistema"), "ElevenLabs" to "ElevenLabs")) {
        voice.updateOutput(output.copy(provider = SpeechProvider.valueOf(it)))
    }
    if (output.provider != SpeechProvider.ElevenLabs) return
    Text(tr("Speech text is sent to ElevenLabs. Voice tests may consume your account credits.",
        "O texto da resposta é enviado à ElevenLabs. Testes de voz podem consumir créditos da sua conta."))
    var key by remember(settings.apiKey) { mutableStateOf(settings.apiKey) }
    OutlinedTextField(key, { key = it }, label = { Text("ElevenLabs API key") }, singleLine = true,
        visualTransformation = PasswordVisualTransformation())
    OutlinedButton(onClick = { voice.updateOutput(output.copy(elevenLabs = settings.copy(apiKey = key.trim()))) }) {
        Text(tr("Save key", "Salvar chave"))
    }
    OutlinedButton(enabled = !voice.loadingCatalog && settings.apiKey.isNotBlank(), onClick = { voice.loadCatalog(scope) }) {
        Text(if (voice.loadingCatalog) tr("Loading…", "Carregando…") else tr("Load voices and models", "Carregar vozes e modelos"))
    }
    ElevenLabsVoiceOptions(voice)
}
