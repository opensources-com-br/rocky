package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
internal fun EvidenceButton(evidence: List<String>) {
    if (evidence.isEmpty()) return
    var open by remember { mutableStateOf(false) }
    TextButton(onClick = { open = true }) { Text(tr("View sources", "Ver fontes")) }
    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text(tr("Cited chat messages", "Mensagens citadas do chat")) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    evidence.forEach { Text("• $it\n") }
                }
            },
            confirmButton = {
                TextButton(onClick = { open = false }) { Text(tr("Close", "Fechar")) }
            },
        )
    }
}
