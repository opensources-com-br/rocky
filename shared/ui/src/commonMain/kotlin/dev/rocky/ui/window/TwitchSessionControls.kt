package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.twitch.TwitchConnectionPhase
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun TwitchSessionControls(twitch: TwitchLiveState, onDisconnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RockyColors.SurfaceElevated)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "CONEXÃO REAL · TWITCH",
                color = RockyColors.Twitch,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = twitch.account?.let { "@${it.login}" } ?: twitch.phase.label,
                modifier = Modifier.padding(top = 2.dp),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
            twitch.detail?.let { detail ->
                Text(
                    text = detail,
                    modifier = Modifier.padding(top = 1.dp),
                    color = RockyColors.TextMuted,
                    fontSize = 10.sp,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        OutlinedButton(
            onClick = onDisconnect,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, RockyColors.Border),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
        ) { Text("Desconectar") }
    }
}

private val TwitchConnectionPhase.label: String
    get() = when (this) {
        TwitchConnectionPhase.Disconnected -> "Desconectada"
        TwitchConnectionPhase.Authenticating -> "Autenticando"
        TwitchConnectionPhase.AwaitingAuthorization -> "Aguardando autorização"
        TwitchConnectionPhase.Connecting -> "Conectando ao chat"
        TwitchConnectionPhase.Connected -> "Conectada"
        TwitchConnectionPhase.Reconnecting -> "Reconectando"
        TwitchConnectionPhase.Failed -> "Falha na conexão"
    }
