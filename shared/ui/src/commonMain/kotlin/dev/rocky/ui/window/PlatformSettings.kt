package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun PlatformSettings(platforms: List<PlatformStatus>) {
    var enabled by remember { mutableStateOf(platforms.associate { it.name to it.enabled }) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle(
            "Contas conectadas",
            "O chat de todas as plataformas ativas vira uma fila única.",
        )
        Column(modifier = Modifier.padding(top = 12.dp)) {
            platforms.forEach { platform ->
                PlatformAccount(
                    platform = platform,
                    checked = enabled[platform.name] == true,
                    onCheckedChange = { checked ->
                        enabled = enabled + (platform.name to checked)
                    },
                )
            }
        }
    }
}

@Composable
private fun PlatformAccount(
    platform: PlatformStatus,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
        color = RockyColors.SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, RockyColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.size(9.dp).background(platform.color(), CircleShape))
            Column(modifier = Modifier.padding(start = 11.dp).weight(1f)) {
                Text(
                    text = platform.name,
                    color = RockyColors.TextPrimary,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = if (platform.enabled) {
                        "${platform.account} · ${platform.audience} no chat"
                    } else {
                        platform.account
                    },
                    color = RockyColors.TextMuted,
                    style = MaterialTheme.typography.caption,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RockyColors.TextPrimary,
                    checkedTrackColor = platform.color(),
                    uncheckedThumbColor = RockyColors.TextPrimary,
                    uncheckedTrackColor = RockyColors.TextMuted,
                ),
            )
        }
    }
}
