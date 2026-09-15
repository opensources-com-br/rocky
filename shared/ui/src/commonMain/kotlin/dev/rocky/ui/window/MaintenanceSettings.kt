package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun MaintenanceSettings(updates: UpdateState, report: () -> String, onExport: (String) -> Boolean,
    onOpen: (String) -> Unit, updateContent: @Composable ColumnScope.() -> Unit = {}) {
    val savedLabel = tr("Diagnostics saved.", "Diagnóstico salvo.")
    val failedLabel = tr("Could not export diagnostics.", "Não foi possível exportar o diagnóstico.")
    val scope = rememberCoroutineScope()
    var preview by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    SettingsPreferenceGroup(tr("Updates", "Atualizações")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(shape = RoundedCornerShape(8.dp), enabled = !updates.checking, onClick = { updates.check(scope) }) { Text(tr("Check for updates", "Verificar atualizações")) }
            updates.notice?.let { Text(it, color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption) }
            updates.available?.let { update ->
                TextButton(onClick = { onOpen(update.url) }) { Text(tr("Open official download", "Abrir download oficial")) }
            }
            updateContent()
        }
    }
    SettingsPreferenceGroup(tr("Diagnostics", "Diagnóstico")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(tr("Preview the local report before exporting it. No keys or chat contents are included.",
                "Confira o relatório local antes de exportar. Não inclui chaves nem conteúdo do chat."),
                color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption)
            OutlinedButton(shape = RoundedCornerShape(8.dp), onClick = { preview = report() }) { Text(tr("Preview diagnostics", "Prévia do diagnóstico")) }
            notice?.let { Text(it, color = RockyColors.TextSecondary, style = MaterialTheme.typography.caption) }
        }
    }
    preview?.let { text ->
        AlertDialog(onDismissRequest = { preview = null }, title = { Text(tr("Local diagnostics", "Diagnóstico local")) },
            text = { Text(text, modifier = Modifier.verticalScroll(rememberScrollState())) },
            confirmButton = { TextButton(onClick = {
                runCatching { onExport(text) }.onSuccess { if (it) { preview = null; notice = savedLabel } }
                    .onFailure { notice = failedLabel; preview = null }
            }) { Text(tr("Export", "Exportar")) } },
            dismissButton = { TextButton(onClick = { preview = null }) { Text(tr("Close", "Fechar")) } })
    }
}
