package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.core.tiktok.TikTokConnectionPhase
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun TikTokAccountSettings(
    initialConfiguration: TikTokConfiguration,
    tiktok: TikTokLiveState,
    onConnect: (TikTokConfiguration) -> Unit,
    onDisconnect: () -> Unit,
) {
    var draft by remember(initialConfiguration) { mutableStateOf(initialConfiguration) }
    PlatformPreferenceGroup("TikTok LIVE", tiktok.statusText, tiktok.statusColor, "tiktok-status") {
        Text(
            "Informe o @usuário que está ao vivo. A leitura não pede senha ou cookie e usa o WebCast público por meio do serviço de conexão Eulerstream.",
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 8.dp),
        )
        OutlinedTextField(
            draft.username,
            { draft = draft.copy(username = it) },
            Modifier.fillMaxWidth().padding(top = 10.dp).testTag("tiktok-username"),
            label = { Text("@usuário") },
            singleLine = true, shape = PlatformFieldShape,
            textStyle = MaterialTheme.typography.body2, colors = platformFieldColors(),
            enabled = !tiktok.phase.isConnecting,
        )
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (tiktok.phase in setOf(TikTokConnectionPhase.Disconnected, TikTokConnectionPhase.Failed)) {
                PrimaryButton("Conectar TikTok", draft.username.trim().removePrefix("@").isNotBlank()) {
                    onConnect(draft.copy(username = draft.username.trim().removePrefix("@").trim()))
                }
            } else {
                OutlinedButton(onClick = onDisconnect, border = BorderStroke(1.dp, RockyColors.Border)) {
                    Text("Desconectar")
                }
            }
        }
    }
}

private val TikTokConnectionPhase.isConnecting get() = this in setOf(
    TikTokConnectionPhase.Connecting,
    TikTokConnectionPhase.Reconnecting,
)

private val TikTokLiveState.statusText get() = detail ?: when (phase) {
    TikTokConnectionPhase.Disconnected -> "Não conectada"
    TikTokConnectionPhase.Connecting -> "Procurando live ativa"
    TikTokConnectionPhase.Connected -> "Conectada"
    TikTokConnectionPhase.Reconnecting -> "Reconectando"
    TikTokConnectionPhase.Failed -> "Falha na conexão"
}

private val TikTokLiveState.statusColor get() = when (phase) {
    TikTokConnectionPhase.Connected, TikTokConnectionPhase.Failed -> RockyColors.TikTok
    else -> RockyColors.TextMuted
}
