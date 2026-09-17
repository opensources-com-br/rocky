package dev.rocky.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
internal class DesktopWindowLifecycle(private val usesTray: Boolean) {
    var mainVisible by mutableStateOf(!usesTray)
        private set
    var settingsRevision by mutableStateOf(0)
        private set

    fun showMain() { mainVisible = true }
    fun toggleMain() { mainVisible = !mainVisible }
    fun requestSettings() { settingsRevision += 1 }
    fun closeMain(): Boolean {
        if (usesTray) mainVisible = false
        return !usesTray
    }
}

internal class DesktopLayoutLifecycle {
    var compact = false
        private set
    private var previousSize = ExpandedSize

    fun toggle(currentSize: DpSize): DpSize {
        if (compact) { compact = false; return previousSize }
        previousSize = currentSize
        compact = true
        return CompactSize
    }
}
