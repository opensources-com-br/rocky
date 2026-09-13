package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.*

@Composable
internal fun SpeechChoice(label: String, selected: String, options: List<Pair<String, String>>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, enabled = options.isNotEmpty()) {
            Text("$label: ${options.firstOrNull { it.first == selected }?.second ?: selected}")
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (id, name) -> DropdownMenuItem(onClick = { expanded = false; onSelect(id) }) { Text(name) } }
        }
    }
}
