package dev.rocky.ui.window

import androidx.compose.material.*
import androidx.compose.runtime.*
import dev.rocky.core.updates.AvailableUpdate

@Composable
internal fun UpdateDownloadSettings(state: UpdateDownloadState, available: AvailableUpdate?, blocked: Boolean) {
    if (!state.supported) return
    val scope = rememberCoroutineScope()
    val progress by state.progress.collectAsState()
    if (state.busy) {
        LinearProgressIndicator(progress)
        Text("${(progress * 100).toInt()}% · Aguarde a verificação do pacote")
        TextButton(onClick = state::cancel) { Text(tr("Cancel", "Cancelar")) }
    } else {
        available?.let { update ->
            OutlinedButton(onClick = { state.download(scope, update) }) {
                Text(tr("Download update", "Baixar atualização"))
            }
        }

    }
    if (blocked) Text(tr("Disconnect your platforms before installing.", "Desconecte suas plataformas antes de instalar."))
    state.notice?.let { Text(it) }
}
