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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PulseContent(
    platforms: List<PlatformStatus>,
    samples: List<dev.rocky.core.live.PulseSample> = emptyList(),
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 3.dp)) {
        PulseTrend(samples, audience = false)
        PulseTrend(samples, audience = true)
        Text(tr("Up to 10 minutes, sampled every 5 seconds. Gaps indicate missing measurements.", "Até 10 minutos, amostras a cada 5 segundos. Lacunas indicam medições ausentes."), style = MaterialTheme.typography.caption)
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
                        if (platform.audience == "—") {
                            "audiência indisponível · ${platform.messagesPerMinute} msg/min"
                        } else {
                            "${platform.audience} assistindo · ${platform.messagesPerMinute} msg/min"
                        }
                    } else {
                        "em breve"
                    },
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(17.dp))
        }
    }
}
