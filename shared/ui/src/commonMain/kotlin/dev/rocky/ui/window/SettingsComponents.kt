package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
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
                        .selectable(active, role = Role.Tab) { onSelect(section) }
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
internal fun SettingsSidebar(
    selected: SettingsSection,
    onSelect: (SettingsSection) -> Unit,
    onDone: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val sections = SettingsSection.entries.filter { it.label.contains(query, ignoreCase = true) }

    Surface(
        modifier = Modifier.width(218.dp).fillMaxHeight(),
        color = RockyColors.Surface,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Rocky", style = MaterialTheme.typography.h6)
                Spacer(Modifier.weight(1f))
                Text(
                    text = tr("Done", "Concluir"),
                    color = RockyColors.Accent,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.clickable(onClick = onDone).padding(6.dp),
                )
            }
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                placeholder = { Text(tr("Search", "Buscar")) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.body2,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = RockyColors.TextPrimary,
                    backgroundColor = RockyColors.SurfaceElevated,
                    cursorColor = RockyColors.Accent,
                    leadingIconColor = RockyColors.TextSecondary,
                    placeholderColor = RockyColors.TextMuted,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Spacer(Modifier.height(14.dp))
            sections.forEach { section ->
                SettingsSidebarItem(
                    section = section,
                    selected = section == selected,
                    onClick = { onSelect(section) },
                )
            }
            if (sections.isEmpty()) {
                Text(
                    text = tr("No settings found", "Nenhum ajuste encontrado"),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    color = RockyColors.TextMuted,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
private fun SettingsSidebarItem(
    section: SettingsSection,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) RockyColors.Accent else Color.Transparent,
                RoundedCornerShape(8.dp),
            )
            .selectable(selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(26.dp),
            color = if (selected) Color.White.copy(alpha = 0.18f) else RockyColors.SurfaceSelected,
            shape = RoundedCornerShape(7.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = RockyColors.TextPrimary,
                )
            }
        }
        Text(
            text = section.label,
            modifier = Modifier.padding(start = 10.dp),
            color = RockyColors.TextPrimary,
            style = MaterialTheme.typography.body2,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
internal fun SettingsSectionHeading(section: SettingsSection) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            color = RockyColors.SurfaceSelected,
            shape = RoundedCornerShape(12.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                    tint = RockyColors.TextPrimary,
                )
            }
        }
        Text(
            text = section.label,
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.h5,
        )
        Text(
            text = section.description,
            modifier = Modifier.padding(top = 4.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.body2,
        )
    }
}

private val SettingsSection.icon: ImageVector
    get() = when (this) {
        SettingsSection.Agent -> Icons.Outlined.Person
        SettingsSection.Ai -> Icons.Outlined.AutoAwesome
        SettingsSection.Voice -> Icons.Outlined.HeadsetMic
        SettingsSection.Platforms -> Icons.Outlined.Hub
        SettingsSection.Data -> Icons.Outlined.Folder
    }

private val SettingsSection.description: String
    @Composable get() = when (this) {
        SettingsSection.Agent -> tr("Name, language, and how Rocky communicates.", "Nome, idioma e como o Rocky se comunica.")
        SettingsSection.Ai -> tr("Provider, model, and response behavior.", "Provedor, modelo e comportamento das respostas.")
        SettingsSection.Voice -> tr("Microphone and speech preferences.", "Preferências de microfone e fala.")
        SettingsSection.Platforms -> tr("Accounts and live-streaming connections.", "Contas e conexões com plataformas de live.")
        SettingsSection.Data -> tr("Local data, updates, backups, and privacy.", "Dados locais, atualizações, backups e privacidade.")
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
    FlowRow(
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
