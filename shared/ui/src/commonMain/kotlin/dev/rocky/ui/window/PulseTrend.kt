package dev.rocky.ui.window

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.PulseSample
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PulseTrend(samples: List<PulseSample>, audience: Boolean) {
    val values = samples.map { if (!it.connected) null else if (audience) it.viewers else it.messagesPerMinute }
    val maximum = values.filterNotNull().maxOrNull()?.coerceAtLeast(1) ?: 1
    Column(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(if (audience) tr("Viewers over time", "Espectadores ao longo do tempo") else tr("Chat messages/min over time", "Mensagens/min ao longo do tempo"))
        if (values.none { it != null }) Text(tr("No measurements available.", "Sem medições disponíveis."))
        else {
            Text("0 – $maximum", style = MaterialTheme.typography.caption)
            Canvas(Modifier.fillMaxWidth().height(95.dp)) {
                val span = ((samples.lastOrNull()?.timeMillis ?: 0) - (samples.firstOrNull()?.timeMillis ?: 0)).coerceAtLeast(5000)
                fun point(index: Int, value: Int) = Offset(
                    (samples[index].timeMillis - samples.first().timeMillis).toFloat() / span * size.width,
                    size.height - value.toFloat() / maximum * size.height)
                values.forEachIndexed { index, value ->
                    if (value != null) {
                        drawCircle(RockyColors.Accent, 2f, point(index, value))
                        val previous = values.getOrNull(index - 1)
                        if (previous != null && samples[index].timeMillis - samples[index - 1].timeMillis <= 10_000)
                            drawLine(RockyColors.Accent, point(index - 1, previous), point(index, value), 2f)
                    }
                }
            }
            val seconds = if (samples.size > 1) (samples.last().timeMillis - samples.first().timeMillis) / 1000 else 0
            Text(tr("Observed window: ", "Janela observada: ") + "$seconds s", style = MaterialTheme.typography.caption)
        }
    }
}
