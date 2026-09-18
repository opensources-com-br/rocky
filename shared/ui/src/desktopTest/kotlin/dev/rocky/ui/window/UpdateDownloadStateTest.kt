package dev.rocky.ui.window

import dev.rocky.core.updates.*
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UpdateDownloadStateTest {
    private class Installer : UpdateInstaller {
        var opened = 0
        var cancelled = 0
        var failure = false
        var openFailure = false
        var onOpen: () -> Unit = {}
        override fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit): PreparedUpdate {
            if (failure) error("offline")
            onProgress(10, 10)
            return PreparedUpdate(update.version, "package", "hash")
        }
        override fun cancel() { cancelled++ }
        override fun open(update: PreparedUpdate) { if (openFailure) error("blocked"); onOpen(); opened++ }
    }
    @Test fun failedInstallerOpenCanBeRetried() = runBlocking {
        val installer = Installer().apply { openFailure = true }
        val state = UpdateDownloadState(installer, this)
        state.download(AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        state.install({ true }, { true })
        withTimeout(5000) { while (state.busy) delay(10) }
        assertEquals(0, installer.opened)
        assertFalse(state.opening)

        installer.openFailure = false
        state.install({ true }, { true })
        withTimeout(5000) { while (state.busy) delay(10) }
        assertEquals(1, installer.opened)
    }
    @Test fun downloadDoesNotInstallAndActiveSessionBlocksOpening() = runBlocking {
        val installer = Installer()
        val state = UpdateDownloadState(installer, this)
        state.download(AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNotNull(state.prepared)
        assertEquals(1f, state.progress.value)
        assertEquals(0, installer.opened)
        state.install({ false }, { true })
        assertEquals(0, installer.opened)
        state.install({ true }, { true })
        withTimeout(5000) { while (state.busy) delay(10) }
        assertEquals(1, installer.opened)
    }
    @Test fun failedDownloadCanBeRetried() = runBlocking {
        val installer = Installer().apply { failure = true }
        val state = UpdateDownloadState(installer, this)
        state.download(AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNull(state.prepared)
        installer.failure = false
        state.download(AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNotNull(state.prepared)
    }
    @Test fun restartRunsOnlyAfterTheHelperIsReadyAndDisposalPreservesHandoff() = runBlocking {
        val installer = Installer()
        val state = UpdateDownloadState(installer, this)
        val helperStarted = CountDownLatch(1)
        val helperReady = CountDownLatch(1)
        installer.onOpen = { helperStarted.countDown(); check(helperReady.await(5, TimeUnit.SECONDS)) }
        state.download(AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        var restarted = false
        state.install({ true }) { restarted = true; state.dispose(); true }
        assertTrue(helperStarted.await(5, TimeUnit.SECONDS))
        assertFalse(restarted)
        assertTrue(state.opening)
        helperReady.countDown()
        withTimeout(5000) { while (state.busy) delay(10) }
        assertTrue(restarted)
        assertTrue(state.restarting)
        assertEquals(0, installer.cancelled)
        state.install({ true }, { error("Must only restart once") })
        assertEquals(1, installer.opened)
    @Test fun cancellationBeforeTheWorkerStartsDoesNotLeaveDownloadBusy() = runBlocking {
        val installer = Installer()
        val state = UpdateDownloadState(installer)
        state.download(this, AvailableUpdate("v2.0.0", ""))
        state.cancel()
        assertEquals(1, installer.cancelled)
        withTimeout(5000) { while (state.busy) delay(10) }
        assertFalse(state.busy)
        assertNull(state.prepared)
        state.download(this, AvailableUpdate("v2.0.0", ""))
        withTimeout(5000) { while (state.busy) delay(10) }
        assertNotNull(state.prepared)
    }
}
