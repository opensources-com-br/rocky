package dev.rocky.ui.window

import androidx.compose.runtime.*
import dev.rocky.core.updates.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

internal class UpdateDownloadState(private val installer: UpdateInstaller?, private val scope: CoroutineScope) {
    val unavailableReason = installer?.installationUnavailableReason()
    val installationNotice = installer?.installationNotice()
    val supported get() = installer != null && unavailableReason == null
    var busy by mutableStateOf(false); private set
    var opening by mutableStateOf(false); private set
    var restarting by mutableStateOf(false); private set
    var prepared by mutableStateOf<PreparedUpdate?>(null); private set
    var notice by mutableStateOf<UpdateDownloadNotice?>(null); private set
    val progress = MutableStateFlow(0f)
    private var job: Job? = null

    fun download(update: AvailableUpdate) {
        val service = installer ?: return
        if (busy || restarting || !supported) return
        busy = true; prepared = null; notice = null; progress.value = 0f
        job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                prepared = interruptibleWork { service.download(update) { bytes, total ->
                    progress.value = if (total > 0) (bytes.toDouble() / total).toFloat().coerceIn(0f, 1f) else 0f
                } }
                notice = UpdateDownloadNotice.Verified
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { notice = UpdateDownloadNotice.DownloadFailed }
            finally { busy = false; job = null }
        }
    }

    fun cancel() {
        if (opening || restarting) return
        job?.cancel()
        installer?.cancel()
        notice = UpdateDownloadNotice.Cancelled
    }

    fun dispose() {
        if (!restarting) { job?.cancel(); installer?.cancel() }
    }

    fun install(canInstall: () -> Boolean, onRestart: () -> Boolean) {
        val service = installer ?: return
        val ready = prepared ?: return
        if (busy || restarting || !canInstall()) return
        busy = true; opening = true; notice = null
        job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                interruptibleWork { service.open(ready) }
                if (!canInstall()) {
                    service.cancel(); notice = UpdateDownloadNotice.SessionActive
                } else {
                    restarting = true
                    if (!onRestart()) {
                        restarting = false; service.cancel(); notice = UpdateDownloadNotice.SaveFailed
                    }
                }
            } catch (error: CancellationException) { if (!restarting) service.cancel(); throw error }
            catch (error: Exception) {
                restarting = false; runCatching { service.cancel() }; notice = UpdateDownloadNotice.InstallFailed
            }
            finally { busy = false; opening = false; job = null }
        }
    }
}
