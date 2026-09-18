package dev.rocky.ui.window

import androidx.compose.runtime.Composable

@Composable
internal fun updateDownloadMessage(notice: UpdateDownloadNotice): String = when (notice) {
    UpdateDownloadNotice.Verified -> tr("Update verified and ready to install.", "Atualização verificada e pronta para instalar.")
    UpdateDownloadNotice.DownloadFailed -> tr("Could not download or verify the update. Try again or use the official download.",
        "Não foi possível baixar ou verificar a atualização. Tente novamente ou use o download oficial.")
    UpdateDownloadNotice.Cancelled -> tr("Download cancelled. You can try again.", "Download cancelado. Você pode tentar novamente.")
    UpdateDownloadNotice.SessionActive -> tr("A platform was connected. Disconnect it and try again.",
        "Uma plataforma foi conectada. Desconecte e tente novamente.")
    UpdateDownloadNotice.SaveFailed -> tr("Could not save your session. Rocky stayed open. Check your data folder and try again.",
        "Não foi possível salvar a sessão. O Rocky permaneceu aberto. Verifique a pasta de dados e tente novamente.")
    UpdateDownloadNotice.InstallFailed -> tr("Could not prepare the update. Rocky stayed open. Try again or use the official download.",
        "Não foi possível preparar a atualização. O Rocky permaneceu aberto. Tente novamente ou use o download oficial.")
}

@Composable
internal fun updateUnavailableMessage(reason: String): String = when (reason) {
