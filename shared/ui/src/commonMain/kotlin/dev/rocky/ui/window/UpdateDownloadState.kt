package dev.rocky.ui.window

import androidx.compose.runtime.*
import dev.rocky.core.updates.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

internal class UpdateDownloadState(private val installer: UpdateInstaller?) {
    val supported get() = installer != null
    var busy by mutableStateOf(false); private set
    var prepared by mutableStateOf<PreparedUpdate?>(null); private set
    var notice by mutableStateOf<String?>(null); private set
    val progress = MutableStateFlow(0f)
    private var job: Job? = null

}
