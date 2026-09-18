package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.core.updates.*
import dev.rocky.ui.theme.RockyTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UpdateExperienceTest {
    @get:Rule val rule = createComposeRule()
    private class Installer : UpdateInstaller {
        var opened = 0
        var downloads = 0
        var failOpening = false
        var onDownload: () -> Unit = {}
        override fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit): PreparedUpdate {
            onDownload()
            downloads++
            onProgress(10, 10)
            return PreparedUpdate(update.version, "test-package", "verified")
        }
        override fun open(update: PreparedUpdate) { if (failOpening) error("Invalid installer"); opened++ }
        override fun cancel() = Unit
    }
    @Test fun portugueseSettingsDownloadsBlocksConnectedSessionAndRestartsWhenDisconnected() {
        val installer = Installer()
        var blocked by mutableStateOf(true)
        var restarted = false
        rule.setContent {
            val state = rememberUpdateDownloadState(installer)
            RockyTheme { CompositionLocalProvider(LocalRockyLanguage provides RockyLanguage.PortugueseBrazil) {
                Column { UpdateDownloadSettings(state, AvailableUpdate("v2.0.0", ""), blocked) {
                    assertEquals(1, installer.opened); restarted = true; true
                } }
            } }
        }
        rule.onNodeWithText("Baixar atualização").performClick()
        rule.waitUntil(5000) { rule.onAllNodesWithTag("update-restart").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Atualizar e reiniciar").assertIsNotEnabled()
        assertEquals(0, installer.opened)
        rule.runOnIdle { blocked = false }
        rule.onNodeWithTag("update-restart").performClick()
        rule.waitUntil(5000) { restarted }
        rule.onNodeWithText("Reiniciando o Rocky…").assertExists()
    }
    @Test fun englishBannerDownloadsAndRestartsWithoutOpeningTheBrowser() {
        val installer = Installer()
        var restarted = false
        rule.setContent {
            val scope = rememberCoroutineScope()
            val updates = remember { UpdateState { AvailableUpdate("v2.0.0", "https://example.com") } }
            val download = rememberUpdateDownloadState(installer)
            LaunchedEffect(Unit) { updates.check(scope) }
            RockyTheme { UpdateBanner(updates, download, false, { error("Must update inside Rocky") }, { restarted = true; true }) }
        }
        rule.waitUntil(5000) { rule.onAllNodesWithTag("update-banner-action").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Update").performClick()
        rule.waitUntil(5000) { rule.onAllNodesWithText("Update and restart").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Update and restart").performClick()
        rule.waitUntil(5000) { restarted }
        assertEquals(1, installer.opened)
    }
    @Test fun unavailableInstallationExplainsTheRequiredSetupInPortuguese() {
        val installer = object : UpdateInstaller {
            override fun installationUnavailableReason() = "not-installed"
            override fun installationNotice() = "update-failed"
            override fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit): PreparedUpdate = error("Unavailable")
            override fun open(update: PreparedUpdate) = error("Unavailable")
            override fun cancel() = Unit
        }
        rule.setContent {
            val state = rememberUpdateDownloadState(installer)
            RockyTheme { CompositionLocalProvider(LocalRockyLanguage provides RockyLanguage.PortugueseBrazil) {
                Column { UpdateDownloadSettings(state, AvailableUpdate("v2.0.0", ""), false) }
            } }
        }
        rule.onNodeWithText("Mova o Rocky para Aplicativos e abra por lá para habilitar atualizações.").assertExists()
        rule.onNodeWithText("A atualização anterior não foi concluída. Seus dados foram preservados. Tente novamente ou use o download oficial.").assertExists()
        rule.onNodeWithTag("update-download").assertDoesNotExist()
    }
    @Test fun downloadingContinuesAfterClosingSettings() {
        val installer = Installer()
        val started = CountDownLatch(1)
        val complete = CountDownLatch(1)
        installer.onDownload = { started.countDown(); check(complete.await(5, TimeUnit.SECONDS)) }
        var showSettings by mutableStateOf(true)
        lateinit var state: UpdateDownloadState
        rule.setContent {
            state = rememberUpdateDownloadState(installer)
            RockyTheme { if (showSettings) Column {
                UpdateDownloadSettings(state, AvailableUpdate("v2.0.0", ""), false)
            } }
        }
        rule.onNodeWithTag("update-download").performClick()
        assertTrue(started.await(5, TimeUnit.SECONDS))
        rule.runOnIdle { showSettings = false }
        complete.countDown()
        rule.waitUntil(5000) { !state.busy }
        assertNotNull(state.prepared)
        assertEquals(0, installer.opened)
    }
    @Test fun failedPreparationOffersRedownloadAndCanRecoverWithoutClosingRocky() {
        val installer = Installer().apply { failOpening = true }
        var restarted = false
        rule.setContent {
            val state = rememberUpdateDownloadState(installer)
            RockyTheme { Column { UpdateDownloadSettings(state, AvailableUpdate("v2.0.0", ""), false) {
                restarted = true; true
            } } }
        }
        rule.onNodeWithTag("update-download").performClick()
        rule.waitUntil(5000) { rule.onAllNodesWithTag("update-restart").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("update-restart").performClick()
        rule.waitUntil(5000) { rule.onAllNodesWithTag("update-redownload").fetchSemanticsNodes().isNotEmpty() }
        assertFalse(restarted)
        rule.runOnIdle { installer.failOpening = false }
        rule.onNodeWithText("Download again").performClick()
        rule.waitUntil(5000) { rule.onAllNodesWithTag("update-restart").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("update-redownload").assertDoesNotExist()
        assertEquals(2, installer.downloads)
        rule.onNodeWithTag("update-restart").performClick()
        rule.waitUntil(5000) { restarted }
        assertEquals(1, installer.opened)
    }
}
