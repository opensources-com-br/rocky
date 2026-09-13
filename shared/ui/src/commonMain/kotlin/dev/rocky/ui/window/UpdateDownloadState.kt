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

    fun download(scope: CoroutineScope, update: AvailableUpdate) {
        val service = installer ?: return
        if (busy) return
        busy = true; prepared = null; notice = null; progress.value = 0f
        job = scope.launch {
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

}
