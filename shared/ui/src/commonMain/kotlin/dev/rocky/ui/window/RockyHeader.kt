package dev.rocky.ui.window

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.twitch.TwitchConnectionPhase
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun RockyHeader(
    agentName: String = "Rocky",
    compact: Boolean = false,
    pinned: Boolean = false,
    sessionStatus: LiveSessionStatus = LiveSessionStatus.Stopped,
    twitchPhase: TwitchConnectionPhase? = null,
    microphoneActive: Boolean = false,
    onTogglePinned: () -> Unit = {},
    onToggleCompact: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = agentName,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = RockyColors.TextPrimary,
            )
            if (!compact) {
                Text(
                    text = "a voz do chat, em acordes",
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
            }
        }
        ListeningBadge(sessionStatus, twitchPhase, microphoneActive)
        IconButton(onClick = onToggleCompact, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = if (compact) Icons.Outlined.OpenInFull else Icons.Outlined.CloseFullscreen,
                contentDescription = if (compact) "Modo expandido" else "Modo compacto",
                tint = RockyColors.TextMuted,
                modifier = Modifier.size(17.dp),
            )
        }
        IconButton(onClick = onTogglePinned, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = Icons.Outlined.PushPin,
                contentDescription = if (pinned) "Desafixar janela" else "Fixar janela",
                tint = if (pinned) RockyColors.Accent else RockyColors.TextMuted,
                modifier = Modifier.size(17.dp),
            )
        }
        if (!compact) {
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Abrir configurações",
                    tint = RockyColors.TextSecondary,
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
private fun ListeningBadge(
    status: LiveSessionStatus,
    twitchPhase: TwitchConnectionPhase?,
    microphoneActive: Boolean,
) {
    val active = twitchPhase == TwitchConnectionPhase.Connected || status == LiveSessionStatus.Running
    Box(
        modifier = Modifier
            .border(1.dp, if (active) RockyColors.AccentMuted else RockyColors.Border, RoundedCornerShape(18.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = when {
                microphoneActive -> "MICROFONE ON"
                twitchPhase == TwitchConnectionPhase.Connected -> "CHAT ATIVO"
                else -> twitchPhase?.badgeLabel ?: status.badgeLabel
            },
            color = if (active) RockyColors.Accent else RockyColors.TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 1.6.sp,
            textAlign = TextAlign.Center,
        )
    }
}

private val LiveSessionStatus.badgeLabel: String
    get() = when (this) {
        LiveSessionStatus.Stopped -> "PARADO"
        LiveSessionStatus.Running -> "OUVINDO"
        LiveSessionStatus.Ended -> "ENCERRADO"
    }

private val TwitchConnectionPhase.badgeLabel: String
    get() = when (this) {
        TwitchConnectionPhase.Disconnected -> "PARADO"
        TwitchConnectionPhase.Authenticating,
        TwitchConnectionPhase.AwaitingAuthorization,
        TwitchConnectionPhase.Connecting -> "CONECTANDO"
        TwitchConnectionPhase.Connected -> "CHAT ATIVO"
        TwitchConnectionPhase.Reconnecting -> "RECONECTANDO"
        TwitchConnectionPhase.Failed -> "ERRO"
    }
