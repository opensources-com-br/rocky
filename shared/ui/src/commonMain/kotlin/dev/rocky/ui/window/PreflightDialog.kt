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
    ai: AiSuggestionState,
    voice: VoiceState,
    agentName: String,
    onConfigure: (SettingsSection) -> Unit,
    onOpenGuide: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val connected = twitch.phase == TwitchConnectionPhase.Connected
    val ready = connected && ai.connectionVerified
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Before going live", "Antes da live")) },
        text = {
            Column(Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (ready) tr("Checks completed", "Verificações concluídas") else tr("Review the items below", "Revise os itens abaixo"),
                    style = MaterialTheme.typography.subtitle1)
                Text(if (connected) tr("Twitch connected", "Twitch conectada") else tr("Twitch disconnected", "Twitch desconectada"))
                TextButton(onClick = { onConfigure(SettingsSection.Platforms) }) { Text(tr("Configure Twitch", "Configurar Twitch")) }
                Text(if (ai.connectionVerified) tr("AI connection verified", "Conexão com IA verificada") else tr("AI not verified", "IA não verificada"))
                ai.status?.let { Text(it, style = MaterialTheme.typography.caption) }
                Row {
                    TextButton(enabled = !ai.testing && ai.isReady, onClick = { ai.testConnection(scope) }) { Text(tr("Test AI", "Testar IA")) }
                    TextButton(onClick = { onConfigure(SettingsSection.Ai) }) { Text(tr("Configure AI", "Configurar IA")) }
                }
                Text(tr("You can continue using text without a microphone.", "Você pode continuar por texto sem microfone."))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Close", "Fechar")) } },
    )
}
