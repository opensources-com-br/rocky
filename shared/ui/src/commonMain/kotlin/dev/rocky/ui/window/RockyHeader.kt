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
import androidx.compose.material.Text
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
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun RockyHeader(
    compact: Boolean = false,
    pinned: Boolean = false,
    onClose: () -> Unit = {},
    onMinimize: () -> Unit = {},
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
                text = "Rocky",
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
        ListeningBadge()
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
private fun ListeningBadge() {
    Box(
        modifier = Modifier
            .border(1.dp, RockyColors.AccentMuted, RoundedCornerShape(18.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "OUVINDO",
            color = RockyColors.Accent,
            fontSize = 11.sp,
            letterSpacing = 1.6.sp,
            textAlign = TextAlign.Center,
        )
    }
}
