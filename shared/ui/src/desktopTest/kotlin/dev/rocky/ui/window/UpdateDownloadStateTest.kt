package dev.rocky.ui.window

import dev.rocky.core.updates.*
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.Assert.*

class UpdateDownloadStateTest {
    private class Installer : UpdateInstaller {
        var opened = 0
        var failure = false
        override fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit): PreparedUpdate {
            if (failure) error("offline")
            onProgress(10, 10)
            return PreparedUpdate(update.version, "package", "hash")
        }
        override fun cancel() {}
        override fun open(update: PreparedUpdate) { opened++ }
    }
    @Test fun downloadDoesNotInstallAndActiveSessionBlocksOpening() = runBlocking {
        val installer = Installer()
        val state = UpdateDownloadState(installer)
        state.download(this, AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNotNull(state.prepared)
        assertEquals(1f, state.progress.value)
        assertEquals(0, installer.opened)
        state.install(this) { false }
        assertEquals(0, installer.opened)
        state.install(this) { true }
        withTimeout(5000) { while (state.busy) delay(10) }
        assertEquals(1, installer.opened)
    }
    @Test fun failedDownloadCanBeRetried() = runBlocking {
        val installer = Installer().apply { failure = true }
        val state = UpdateDownloadState(installer)
        state.download(this, AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNull(state.prepared)
        installer.failure = false
        state.download(this, AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNotNull(state.prepared)
    }
}
