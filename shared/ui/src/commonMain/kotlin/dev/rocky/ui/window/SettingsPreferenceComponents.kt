package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun SettingsPreferenceGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            color = RockyColors.TextSecondary,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Medium,
        )
        Surface(color = RockyColors.SurfaceElevated, shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    }
}

@Composable
internal fun SettingsPreferenceRow(
    title: String,
    description: String? = null,
    stackWhenCompact: Boolean = false,
    control: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        val stacked = stackWhenCompact && maxWidth < 440.dp
        val label: @Composable () -> Unit = {
            Column {
                Text(title, style = MaterialTheme.typography.body2, fontWeight = FontWeight.Medium)
                description?.let {
                    Text(it, modifier = Modifier.padding(top = 4.dp), color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption)
                }
            }
        }
        if (stacked) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                label()
                control()
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(modifier = Modifier.weight(1f)) { label() }
                Box(modifier = Modifier.width(190.dp)) { control() }
            }
        }
    }
}

@Composable
internal fun SettingsPreferenceMenu(options: List<String>, selected: String, tag: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 36.dp).testTag(tag),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, RockyColors.Border),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = RockyColors.Surface,
                contentColor = RockyColors.TextPrimary),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(selected, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp),
                tint = RockyColors.TextSecondary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = { expanded = false; onSelect(option) }) {
                    Text(option, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2)
                    Spacer(Modifier.width(18.dp))
                    if (option == selected) {
                        Icon(Icons.Outlined.Check, contentDescription = tr("Selected", "Selecionado"),
                            modifier = Modifier.size(16.dp), tint = RockyColors.Accent)
                    }
                }
            }
        }
    }
}
