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
}
