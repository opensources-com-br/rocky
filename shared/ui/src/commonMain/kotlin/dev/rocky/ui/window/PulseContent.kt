package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PulseContent(platforms: List<PlatformStatus>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 3.dp)) {
        platforms.forEach { platform ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = platform.name,
                    color = if (platform.enabled) RockyColors.TextSecondary else RockyColors.TextMuted,
                    style = MaterialTheme.typography.body2,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (platform.enabled) {
                        "${platform.audience} assistindo · ${platform.messagesPerMinute} msg/min"
                    } else {
                        "desconectado"
                    },
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(7.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .background(RockyColors.SurfaceElevated, RoundedCornerShape(6.dp)),
            ) {
                if (platform.enabled) {
                    Spacer(
                        Modifier
                            .fillMaxWidth(platform.audience.toFloat() / 820f)
                            .height(7.dp)
                            .background(platform.color(), RoundedCornerShape(6.dp)),
                    )
                }
            }
            Spacer(Modifier.height(17.dp))
        }
    }
}
