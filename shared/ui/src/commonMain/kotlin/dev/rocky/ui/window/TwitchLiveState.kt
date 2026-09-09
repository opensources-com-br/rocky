package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionPhase

internal class TwitchLiveState(private val client: TwitchChatClient) {
    val messages = mutableStateListOf<ChatMessage>()

    var phase by mutableStateOf(TwitchConnectionPhase.Disconnected)
        private set

    var detail by mutableStateOf<String?>(null)
        private set

    var account by mutableStateOf<TwitchAccount?>(null)
        private set

    var userCode by mutableStateOf<String?>(null)
        private set

    var verificationUri by mutableStateOf<String?>(null)
        private set

    val isRealSession: Boolean
        get() = phase != TwitchConnectionPhase.Disconnected

    fun connect(clientId: String) {
        if (clientId.isBlank()) {
            phase = TwitchConnectionPhase.Failed
            detail = "Informe o Client ID da Twitch."
            return
        }
        messages.clear()
        account = null
        userCode = null
        verificationUri = null
        client.connect(clientId, ::receive)
    }

    fun disconnect() {
        client.disconnect()
    }

    private fun receive(event: TwitchConnectionEvent) {
        Snapshot.withMutableSnapshot {
            when (event) {
                is TwitchConnectionEvent.PhaseChanged -> {
                    phase = event.phase
                    detail = event.detail
                    if (event.phase == TwitchConnectionPhase.Disconnected) {
                        account = null
                        userCode = null
                        verificationUri = null
                    }
                }
                is TwitchConnectionEvent.AuthorizationRequired -> {
                    phase = TwitchConnectionPhase.AwaitingAuthorization
                    detail = "Confirme o código no navegador."
                    userCode = event.userCode
                    verificationUri = event.verificationUri
                }
                is TwitchConnectionEvent.Connected -> {
                    phase = TwitchConnectionPhase.Connected
                    detail = "Recebendo o chat de @${event.account.login}"
                    account = event.account
                    userCode = null
                    verificationUri = null
                }
                is TwitchConnectionEvent.MessageReceived -> messages += event.message
            }
        }
    }
}
