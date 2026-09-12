package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
internal fun MaintenanceSettings(updates: UpdateState, report: () -> String, onExport: (String) -> Boolean,
    onOpen: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    var preview by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    Column {
        OutlinedButton(enabled = !updates.checking, onClick = { updates.check(scope) }) { Text(tr("Check for updates", "Verificar atualizações")) }
        updates.notice?.let { Text(it) }
        updates.available?.let { update ->
            TextButton(onClick = { onOpen(update.url) }) { Text(tr("Open official download", "Abrir download oficial")) }
        }
        OutlinedButton(onClick = { preview = report() }) { Text(tr("Preview diagnostics", "Prévia do diagnóstico")) }
        notice?.let { Text(it) }
    }
    preview?.let { text ->
        AlertDialog(onDismissRequest = { preview = null }, title = { Text(tr("Local diagnostics", "Diagnóstico local")) },
            text = { Text(text, modifier = Modifier.verticalScroll(rememberScrollState())) },
            confirmButton = { TextButton(onClick = {
                runCatching { onExport(text) }.onSuccess { if (it) { preview = null; notice = tr("Diagnostics saved.", "Diagnóstico salvo.") } }
                    .onFailure { notice = tr("Could not export diagnostics.", "Não foi possível exportar o diagnóstico."); preview = null }
            }) { Text(tr("Export", "Exportar")) } },
            dismissButton = { TextButton(onClick = { preview = null }) { Text(tr("Close", "Fechar")) } })
    }
}
