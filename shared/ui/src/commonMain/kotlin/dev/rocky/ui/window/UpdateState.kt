package dev.rocky.ui.window

import androidx.compose.runtime.*
import dev.rocky.core.updates.AvailableUpdate
import kotlinx.coroutines.*

internal class UpdateState(private val checker: () -> AvailableUpdate?) {
    var checking by mutableStateOf(false); private set
    var available by mutableStateOf<AvailableUpdate?>(null); private set
    var notice by mutableStateOf<String?>(null); private set
    var dismissed by mutableStateOf(false)
    fun check(scope: CoroutineScope) {
        if (checking) return
        checking = true
        scope.launch {
            try {
                val result = runCatching { interruptibleWork(checker) }
                result.onSuccess {
                    available = it; dismissed = false
                    notice = if (it == null) "Nenhuma versão mais recente no seu canal." else "Nova versão disponível: ${it.version}"
                }.onFailure {
                    if (it is CancellationException) throw it
                    notice = "Não foi possível verificar atualizações. Tente novamente."
                }
            } finally { checking = false }
        }
    }
}
