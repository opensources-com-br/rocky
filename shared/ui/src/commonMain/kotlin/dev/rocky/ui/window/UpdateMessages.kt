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
    "development-build" -> tr("Install a released version of Rocky to enable updates in the app.",
        "Instale uma versão publicada do Rocky para habilitar atualizações no aplicativo.")
    "not-installed" -> tr("Move Rocky to Applications and open it there to enable updates.",
        "Mova o Rocky para Aplicativos e abra por lá para habilitar atualizações.")
    "read-only-installation" -> tr("Rocky needs write access to its installation folder. Use the official installer to update this copy.",
        "O Rocky precisa de acesso de escrita à pasta de instalação. Use o instalador oficial para atualizar esta cópia.")
    else -> tr("Use the official download to update Rocky on this platform.", "Use o download oficial para atualizar o Rocky nesta plataforma.")
}

@Composable
internal fun updateInstallationMessage(notice: String): String = when (notice) {
    "update-installed" -> tr("Rocky was updated successfully.", "O Rocky foi atualizado com sucesso.")
    "restart-required" -> tr("The update was installed and your data was preserved. Restart your computer when convenient to finish.",
        "A atualização foi instalada e seus dados foram preservados. Reinicie o computador quando for conveniente para concluir.")
    "update-interrupted" -> tr("The previous update was interrupted. Check for updates to try again.",
        "A atualização anterior foi interrompida. Verifique atualizações para tentar novamente.")
    else -> tr("The previous update could not be completed. Your data was preserved. Try again or use the official download.",
        "A atualização anterior não foi concluída. Seus dados foram preservados. Tente novamente ou use o download oficial.")
}
