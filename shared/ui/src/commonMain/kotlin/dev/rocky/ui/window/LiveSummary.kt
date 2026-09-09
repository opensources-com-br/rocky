package dev.rocky.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.core.live.RockySuggestion
import dev.rocky.core.live.StreamPlatform
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PlatformStrip(platforms: List<PlatformStatus>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        platforms.forEach { platform ->
            Row(
                modifier = Modifier
                    .border(1.dp, RockyColors.Border, RoundedCornerShape(18.dp))
                    .background(RockyColors.SurfaceElevated, RoundedCornerShape(18.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(platform.color(), CircleShape),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = platform.name,
                    color = if (platform.enabled) RockyColors.TextPrimary else RockyColors.TextMuted,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
internal fun LiveSummary(
    suggestion: RockySuggestion? = previewSuggestion,
    sourceCounts: Map<StreamPlatform, Int> = previewSourceCounts,
    suggestionSaved: Boolean = false,
    silenced: Boolean = false,
    onSaveNote: () -> Unit,
    onNext: () -> Unit,
    onSilence: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A1B17), Color(0xFF171315)),
                ),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.MusicNote,
                contentDescription = null,
                tint = RockyColors.Accent,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (suggestion == null) "OUVINDO O CHAT" else "TOCANDO AGORA",
                color = RockyColors.Accent,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.GraphicEq,
                contentDescription = null,
                tint = RockyColors.Accent,
                modifier = Modifier.size(42.dp, 26.dp),
            )
        }
        Spacer(Modifier.height(17.dp))
        Text(
            text = suggestion?.text ?: "Estou acompanhando as mensagens para encontrar algo útil.",
            style = MaterialTheme.typography.h1,
        )
        Spacer(Modifier.height(13.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SourceCount(sourceCounts[StreamPlatform.Twitch] ?: 0, "Twitch", RockyColors.Twitch)
            SourceCount(sourceCounts[StreamPlatform.YouTube] ?: 0, "YouTube", RockyColors.YouTube)
            SourceCount(sourceCounts[StreamPlatform.Kick] ?: 0, "Kick", RockyColors.Kick)
        }
        Spacer(Modifier.height(17.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = onSaveNote,
                enabled = suggestion != null && !suggestionSaved,
                shape = RoundedCornerShape(11.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = RockyColors.Accent,
                    contentColor = Color.Black,
                ),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp),
            ) {
                Text(
                    text = if (suggestionSaved) "Nota salva" else "Salvar como nota",
                    fontWeight = FontWeight.Bold,
                )
            }
            PromptAction("Próxima", onNext, enabled = suggestion != null)
            PromptAction(if (silenced) "Retomar" else "Silenciar", onSilence)
        }
    }
}

@Composable
private fun SourceCount(count: Int, platform: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$count ${if (platform == "Twitch") "na" else "no"} $platform",
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun PromptAction(label: String, onClick: () -> Unit, enabled: Boolean = true) {
    OutlinedButton(
        modifier = Modifier.height(42.dp),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(11.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RockyColors.Border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = RockyColors.TextPrimary),
    ) {
        Text(label, fontWeight = FontWeight.Normal)
    }
}

private val previewSuggestion = RockySuggestion(
    id = "preview",
    text = "Sete pessoas perguntaram o preço do curso nos últimos dois minutos. Vale responder agora.",
    sourceMessageIds = emptySet(),
)

private val previewSourceCounts = mapOf(
    StreamPlatform.Twitch to 4,
    StreamPlatform.YouTube to 2,
    StreamPlatform.Kick to 1,
)

internal fun PlatformStatus.color(): Color = when (colorKey) {
    PlatformColor.Twitch -> RockyColors.Twitch
    PlatformColor.Kick -> RockyColors.Kick
    PlatformColor.YouTube -> RockyColors.YouTube
    PlatformColor.Offline -> RockyColors.Offline
}
