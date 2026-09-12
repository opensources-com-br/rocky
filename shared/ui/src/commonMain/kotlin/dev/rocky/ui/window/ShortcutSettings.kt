package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ShortcutSettings(keys: List<Int>, available: Boolean?, onSave: (List<Int>) -> Unit) {
    var draft by remember(keys) { mutableStateOf(keys.map { it.toString() }) }
    val values = draft.map { it.toIntOrNull() ?: 0 }
    val valid = values.all { it in 1..12 } && values.distinct().size == 3
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Text(tr("Global shortcuts · Ctrl + Shift + F1–F12", "Atalhos globais · Ctrl + Shift + F1–F12"))
        Text(when (available) {
            true -> tr("Shortcuts active", "Atalhos ativos")
            false -> tr("Shortcut unavailable or already in use. Choose different keys.", "Atalho indisponível ou em uso. Escolha outras teclas.")
            null -> tr("Registering shortcuts…", "Registrando atalhos…")
        }, style = MaterialTheme.typography.caption)
        val labels = listOf(tr("Microphone on/off", "Microfone liga/desliga"), tr("Mute/unmute", "Silenciar/retomar"), tr("Show/hide", "Mostrar/ocultar"))
        labels.forEachIndexed { index, label ->
            OutlinedTextField(value = draft[index], onValueChange = { value ->
                draft = draft.toMutableList().also { it[index] = value.filter(Char::isDigit).take(2) }
            }, label = { Text("$label · F") }, singleLine = true)
        }
        if (!valid) Text(tr("Use three different numbers from 1 to 12.", "Use três números diferentes de 1 a 12."))
        TextButton(enabled = valid, onClick = { onSave(values) }) { Text(tr("Apply shortcuts", "Aplicar atalhos")) }
    }
}
