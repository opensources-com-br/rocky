package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun CredentialVisibilityButton(visible: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
            contentDescription = if (visible) "Ocultar valor" else "Mostrar valor",
            tint = RockyColors.TextSecondary,
        )
    }
}

@Composable
internal fun SettingsHeading(onDone: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Configurações", style = MaterialTheme.typography.h6)
        Spacer(Modifier.weight(1f))
        Text(
            text = "concluir",
            color = RockyColors.Accent,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.clickable(onClick = onDone).padding(6.dp),
        )
    }
}

@Composable
internal fun SettingsNavigation(
    selected: SettingsSection,
    onSelect: (SettingsSection) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        color = RockyColors.SurfaceElevated,
        shape = RoundedCornerShape(13.dp),
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            SettingsSection.entries.forEach { section ->
                val active = section == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (active) RockyColors.SurfaceSelected else Color.Transparent,
                            RoundedCornerShape(9.dp),
                        )
                        .clickable { onSelect(section) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = section.label,
                        color = if (active) RockyColors.TextPrimary else RockyColors.TextSecondary,
                        style = MaterialTheme.typography.body2,
                        fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
internal fun SettingTitle(
    title: String,
    description: String,
    value: String? = null,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = RockyColors.TextPrimary, style = MaterialTheme.typography.body1)
        Spacer(Modifier.weight(1f))
        value?.let {
            Text(it, color = RockyColors.TextSecondary, style = MaterialTheme.typography.body2)
        }
    }
    Text(
        text = description,
        modifier = Modifier.padding(top = 4.dp),
        color = RockyColors.TextMuted,
        style = MaterialTheme.typography.body2,
    )
}

@Composable
internal fun ChoiceRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        options.forEach { option ->
            val active = option == selected
            Surface(
                modifier = Modifier.clickable { onSelect(option) },
                color = if (active) Color(0xFF2C1C17) else RockyColors.SurfaceElevated,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    if (active) RockyColors.AccentMuted else RockyColors.Border,
                ),
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = if (active) RockyColors.Accent else RockyColors.TextSecondary,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
internal fun RockySlider(value: Float, enabled: Boolean = true, onValueChange: (Float) -> Unit) {
    Slider(
        modifier = Modifier.height(36.dp),
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        colors = SliderDefaults.colors(
            thumbColor = RockyColors.Accent,
            activeTrackColor = RockyColors.Accent,
            inactiveTrackColor = RockyColors.TextMuted,
        ),
    )
}

@Composable
internal fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            modifier = modifier.height(34.dp),
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = RockyColors.TextPrimary,
                checkedTrackColor = RockyColors.Accent,
                uncheckedThumbColor = RockyColors.TextPrimary,
                uncheckedTrackColor = RockyColors.TextMuted,
            ),
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.body2,
        )
    }
}
