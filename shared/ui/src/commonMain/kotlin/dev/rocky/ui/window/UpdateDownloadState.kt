package dev.rocky.ui.window

import androidx.compose.runtime.*
import dev.rocky.core.updates.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

internal class UpdateDownloadState(private val installer: UpdateInstaller?) {
    val supported get() = installer != null
    var busy by mutableStateOf(false); private set
    var opening by mutableStateOf(false); private set
    var prepared by mutableStateOf<PreparedUpdate?>(null); private set
    var notice by mutableStateOf<String?>(null); private set
    val progress = MutableStateFlow(0f)
    private var job: Job? = null

    fun download(scope: CoroutineScope, update: AvailableUpdate) {
        val service = installer ?: return
        if (busy) return
        busy = true; prepared = null; notice = null; progress.value = 0f
        job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                prepared = interruptibleWork { service.download(update) { bytes, total ->
                    progress.value = if (total > 0) (bytes.toDouble() / total).toFloat().coerceIn(0f, 1f) else 0f
                } }
                notice = "Download verificado. O instalador está pronto para abrir."
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { notice = "Não foi possível baixar ou verificar o instalador. Tente novamente ou use o download oficial." }
            finally { busy = false; job = null }
        }
    }

    fun cancel() {
        job?.cancel()
        installer?.cancel()
        notice = "Download cancelado. Você pode tentar novamente."
    }

    fun install(scope: CoroutineScope, canInstall: () -> Boolean) {
        val service = installer ?: return
        val ready = prepared ?: return
        if (busy || !canInstall()) return
        busy = true; opening = true
        job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                interruptibleWork { service.open(ready) }
                notice = "Instalador aberto. Feche o Rocky, conclua a instalação e abra o app novamente."
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { notice = "Não foi possível abrir o instalador. Tente baixar novamente ou use o download oficial." }
            finally { busy = false; opening = false; job = null }
        }
    }
}
