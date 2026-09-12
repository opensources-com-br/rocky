package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun KickSessionControls(kick: KickLiveState, onDisconnect: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(RockyColors.SurfaceElevated)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("CONEXÃO REAL · KICK", color = RockyColors.Kick, fontSize = 10.sp,
                letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold)
            Text(kick.account?.let { "@${it.username}" } ?: "Conectando",
                modifier = Modifier.padding(top = 2.dp), color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption)
            kick.detail?.let {
                Text(it, modifier = Modifier.padding(top = 1.dp), color = RockyColors.TextMuted, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.width(12.dp))
        OutlinedButton(onClick = onDisconnect, shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, RockyColors.Border),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary)) {
            Text("Desconectar")
        }
    }
}
