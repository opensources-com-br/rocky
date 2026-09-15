package dev.rocky.ui.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun SpeechChoice(label: String, selected: String, options: List<Pair<String, String>>, tag: String? = null, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    SettingsPreferenceRow(label) {
        Box {
            OutlinedButton(
                onClick = { expanded = true }, enabled = options.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().heightIn(min = 36.dp).then(
                    if (tag != null) Modifier.testTag(tag) else Modifier),
                shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, RockyColors.Border),
                colors = ButtonDefaults.outlinedButtonColors(backgroundColor = RockyColors.Surface, contentColor = RockyColors.TextPrimary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            ) {
                Text(options.firstOrNull { it.first == selected }?.second ?: selected,
                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2)
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = RockyColors.TextSecondary)
            }
            DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (id, name) ->
                    DropdownMenuItem(onClick = { expanded = false; onSelect(id) }) {
                        Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2)
                        Spacer(Modifier.width(18.dp))
                        if (id == selected) Icon(Icons.Outlined.Check, contentDescription = tr("Selected", "Selecionado"),
                            modifier = Modifier.size(16.dp), tint = RockyColors.Accent)
                    }
                }
            }
        }
    }
}
