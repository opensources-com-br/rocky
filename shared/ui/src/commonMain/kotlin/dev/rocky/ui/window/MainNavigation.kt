package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun MainNavigation(
    selected: MainSection,
    onSelect: (MainSection) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        color = RockyColors.SurfaceElevated,
        shape = RoundedCornerShape(13.dp),
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            MainSection.entries.forEach { section ->
                val selectedSection = section == selected
                val sectionWeight = when (section) {
                    MainSection.Conversation -> 1.25f
                    MainSection.Support -> 1.15f
                    else -> 0.85f
                }
                Box(
                    modifier = Modifier
                        .weight(sectionWeight)
                        .background(
                            if (selectedSection) RockyColors.SurfaceSelected else Color.Transparent,
                            RoundedCornerShape(9.dp),
                        )
                        .clickable { onSelect(section) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = section.label,
                        color = if (selectedSection) RockyColors.TextPrimary else RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption,
                        fontWeight = if (selectedSection) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

@Composable
internal fun AssistantFooter(
    agentName: String = "Rocky",
    active: Boolean = false,
    capturing: Boolean = false,
    inputLevel: Float = 0f,
    busy: Boolean = false,
    status: String? = null,
    viewerCount: Int? = null,
    messagesPerMinute: Int = 0,
    onTalk: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable(onClick = onTalk)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoicePulse(active = active, capturing = capturing, inputLevel = inputLevel)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when {
                    busy -> "Rocky está transcrevendo"
                    capturing -> "Microfone ouvindo"
                    active -> "Ouvinte ativo"
                    else -> "Ativar ouvinte do $agentName"
                },
                color = RockyColors.TextPrimary,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = when {
                    capturing -> "Fale agora · nível ${(inputLevel * 100).toInt()}%"
                    else -> status ?: if (active) "diga “Rocky” e faça sua pergunta" else "clique para ativar"
                },
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }
        FooterMetric(value = viewerCount?.toString() ?: "—", label = "assistindo")
        Box(Modifier.padding(horizontal = 8.dp).size(1.dp, 28.dp).background(RockyColors.Border))
        FooterMetric(value = messagesPerMinute.toString(), label = "msg/min")
    }
}

@Composable
private fun VoicePulse(active: Boolean, capturing: Boolean, inputLevel: Float) {
    val normalizedLevel = inputLevel.coerceIn(0f, 1f)
    val scale = if (capturing) 1f + normalizedLevel * 2.5f else 1f
    Row(
        modifier = Modifier.width(34.dp).testTag("microphone-level"),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(4, 7, 10, 7, 4).forEach { height ->
            Box(
                Modifier
                    .width(4.dp)
                    .height((height * scale).dp)
                    .background(if (active) RockyColors.Accent else RockyColors.TextMuted, CircleShape),
            )
        }
    }
}

@Composable
private fun FooterMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            color = RockyColors.TextPrimary,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.SemiBold,
        )
        Text(text = label, color = RockyColors.TextMuted, style = MaterialTheme.typography.caption)
    }
}
