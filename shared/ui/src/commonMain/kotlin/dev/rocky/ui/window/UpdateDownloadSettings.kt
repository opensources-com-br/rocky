package dev.rocky.ui.window

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.rocky.core.updates.AvailableUpdate

@Composable
internal fun UpdateDownloadSettings(state: UpdateDownloadState, available: AvailableUpdate?, blocked: Boolean,
    onRestart: () -> Boolean = { false }) {
    val progress by state.progress.collectAsState()
    val latestBlocked by rememberUpdatedState(blocked)
    val latestRestart by rememberUpdatedState(onRestart)
    state.installationNotice?.let { Text(updateInstallationMessage(it)) }
    state.unavailableReason?.let { Text(updateUnavailableMessage(it)) }
    if (!state.supported) return
    when {
        state.restarting -> Text(tr("Restarting Rocky…", "Reiniciando o Rocky…"))
        state.opening -> {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text(tr("Preparing update and restart…", "Preparando atualização e reinício…"))
            OutlinedButton(onClick = { state.download(scope, update) }) {
                Text(tr("Download update", "Baixar atualização"))
            }
        }
        state.prepared?.let {
            Text("${tr("Ready to install", "Pronto para instalar")}: ${it.version}")
            OutlinedButton(enabled = !blocked, onClick = { confirm = true }) {
                Text(tr("Open verified installer", "Abrir instalador verificado"))
            }
        }
    }
    if (blocked) Text(tr("Disconnect your platforms before installing.", "Desconecte suas plataformas antes de instalar."))
    state.notice?.let { Text(it) }
    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        title = { Text(tr("Install update", "Instalar atualização")) },
        text = { Text(tr("The system installer will open. Close Rocky before completing the installation, then reopen it. On macOS, replace Rocky in Applications. Your records and settings stay in the data directory.",
            "O instalador do sistema será aberto. Feche o Rocky antes de concluir e reabra após instalar. No macOS, substitua Rocky em Aplicativos. Registros e configurações permanecem na pasta de dados.")) },
        confirmButton = { TextButton(enabled = !blocked, onClick = {
            confirm = false; state.install(scope) { !latestBlocked }
        }) { Text(tr("Open installer", "Abrir instalador")) } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text(tr("Later", "Depois")) } },
    )
}
