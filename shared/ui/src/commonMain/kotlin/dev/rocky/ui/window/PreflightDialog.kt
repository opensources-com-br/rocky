package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.core.twitch.TwitchConnectionPhase

@Composable
internal fun PreflightDialog(
    scope: kotlinx.coroutines.CoroutineScope,
    twitch: TwitchLiveState,
    kick: KickLiveState,
    ai: AiSuggestionState,
    voice: VoiceState,
    agentName: String,
    onConfigure: (SettingsSection) -> Unit,
    onOpenGuide: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var useVoice by remember { mutableStateOf(false) }
    var heardVoice by remember(voice.configuration.output) { mutableStateOf(false) }
    var obsChecked by remember(voice.configuration.output) { mutableStateOf(false) }
    val connectedPlatform = when {
        twitch.phase == TwitchConnectionPhase.Connected -> "Twitch"
        kick.isConnected -> "Kick"
        else -> null
    }
    val connected = connectedPlatform != null
    val ready = connected && ai.connectionVerified && (!useVoice ||
        (voice.voiceTested && voice.conversationTestTranscript != null && heardVoice && obsChecked))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Before going live", "Antes da live")) },
        text = {
            Column(Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (ready) tr("Checks completed", "Verificações concluídas") else tr("Review the items below", "Revise os itens abaixo"),
                    style = MaterialTheme.typography.subtitle1)
                Text(connectedPlatform?.let { tr("$it connected", "$it conectada") }
                    ?: tr("Streaming platform disconnected", "Plataforma de streaming desconectada"))
                TextButton(onClick = { onConfigure(SettingsSection.Platforms) }) {
                    Text(tr("Configure platform", "Configurar plataforma"))
                }
                Text(if (ai.connectionVerified) tr("AI connection verified", "Conexão com IA verificada") else tr("AI not verified", "IA não verificada"))
                ai.status?.let { Text(it, style = MaterialTheme.typography.caption) }
                Row {
                    TextButton(enabled = !ai.testing && ai.isReady, onClick = { ai.testConnection(scope) }) { Text(tr("Test AI", "Testar IA")) }
                    TextButton(onClick = { onConfigure(SettingsSection.Ai) }) { Text(tr("Configure AI", "Configurar IA")) }
                }
                SettingSwitch(tr("Use voice this session", "Usar voz nesta sessão"), useVoice, { useVoice = it })
                if (useVoice) {
                    Text(tr("1. Test the system voice and confirm you heard it.", "1. Teste a voz do sistema e confirme que ouviu."))
                    TextButton(enabled = !voice.speaking, onClick = { voice.testVoice(scope, agentName) }) { Text(tr("Play test voice", "Ouvir voz de teste")) }
                    SettingSwitch(tr("I heard the voice", "Ouvi a voz"), heardVoice, { heardVoice = it })
                    Text(tr("2. Test a short microphone phrase and check the transcription.", "2. Teste uma frase curta no microfone e confira a transcrição."))
                    TextButton(enabled = voice.transcriptionReady && !voice.conversationTesting,
                        onClick = { voice.testConversation(scope, agentName) }) { Text(tr("Test microphone", "Testar microfone")) }
                    voice.conversationTestTranscript?.let { Text(it) }
                    voice.status?.let { Text(it, style = MaterialTheme.typography.caption) }
                    TextButton(onClick = { onConfigure(SettingsSection.Voice) }) { Text(tr("Configure audio and shortcuts", "Configurar áudio e atalhos")) }
                    Text(tr("3. In OBS, record a short test with the same audio sources you will use live. Play Rocky's voice and listen to the recording.",
                        "3. No OBS, grave um teste curto com as mesmas fontes de áudio da live. Reproduza a voz do Rocky e ouça a gravação."))
                    Text(tr("Windows: review application capture and Desktop Audio. macOS: review screen/audio capture sources. Rocky uses the system output; headphones alone do not exclude it from OBS.",
                        "Windows: revise captura por aplicativo e Áudio do desktop. macOS: revise fontes de captura de tela/áudio. Rocky usa a saída do sistema; fones sozinhos não o excluem do OBS."))
                    TextButton(onClick = { onOpenGuide("https://obsproject.com/kb/application-audio-capture-guide") }) { Text(tr("OBS audio guide (Windows)", "Guia de áudio OBS (Windows)")) }
                    TextButton(onClick = { onOpenGuide("https://obsproject.com/kb/macos-desktop-audio-capture-guide") }) { Text(tr("OBS audio guide (macOS)", "Guia de áudio OBS (macOS)")) }
                    SettingSwitch(tr("I reviewed the recording and confirmed the intended audio", "Revisei a gravação e confirmei o áudio desejado"), obsChecked, { obsChecked = it })
                } else Text(tr("You can continue using text without a microphone.", "Você pode continuar por texto sem microfone."))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Close", "Fechar")) } },
    )
}
