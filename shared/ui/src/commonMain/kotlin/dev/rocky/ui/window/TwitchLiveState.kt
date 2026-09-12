package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionPhase

internal class TwitchLiveState(
    private val client: TwitchChatClient,
    private val currentTimeMillis: () -> Long = { 0L },
) {
    val messages = mutableStateListOf<ChatMessage>()
    private val receivedAtByMessageId = mutableMapOf<String, Long>()
    // One bucket per second, independent of the chat rate.
    private val receivedMessageTimes = mutableStateMapOf<Long, Int>()
    internal val metricBucketCount: Int get() = receivedMessageTimes.size
    private var sessionGeneration = 0L

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

    var totalMessages by mutableStateOf(0)
        private set

    var messagesPerMinute by mutableStateOf(0)
        private set

    var viewerCount by mutableStateOf<Int?>(null)
        private set

    fun connect(clientId: String) {
        if (clientId.isBlank()) {
            phase = TwitchConnectionPhase.Failed
            detail = "Informe o Client ID da Twitch."
            return
        }
        messages.clear()
        receivedAtByMessageId.clear()
        receivedMessageTimes.clear()
        totalMessages = 0
        messagesPerMinute = 0
        viewerCount = null
        account = null
        userCode = null
        verificationUri = null
        val generation = ++sessionGeneration
        client.connect(clientId) { event ->
            if (generation == sessionGeneration) receive(event)
        }
    }

    fun disconnect() {
        sessionGeneration += 1
        client.disconnect()
        Snapshot.withMutableSnapshot {
            phase = TwitchConnectionPhase.Disconnected
            detail = null
            account = null
            viewerCount = null
            userCode = null
            verificationUri = null
        }
    }

    private fun receive(event: TwitchConnectionEvent) {
        Snapshot.withMutableSnapshot {
            when (event) {
                is TwitchConnectionEvent.PhaseChanged -> {
                    phase = event.phase
                    detail = event.detail
                    if (event.phase == TwitchConnectionPhase.Failed) viewerCount = null
                    if (event.phase == TwitchConnectionPhase.Disconnected) {
                        account = null
                        viewerCount = null
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
                is TwitchConnectionEvent.AudienceUpdated -> viewerCount = event.viewerCount
                is TwitchConnectionEvent.MessageReceived -> {
                    if (messages.size == MAX_CHAT_MESSAGES) {
                        receivedAtByMessageId.remove(messages.removeAt(0).id)
                    }
                    messages += event.message
                    receivedAtByMessageId[event.message.id] = currentTimeMillis()
                    totalMessages += 1
                    val second = currentTimeMillis() / 1_000
                    receivedMessageTimes[second] = (receivedMessageTimes[second] ?: 0) + 1
                    refreshMetrics()
                }
            }
        }
    }

    fun refreshMetrics() = Snapshot.withMutableSnapshot {
        val cutoff = currentTimeMillis() / 1_000 - 59
        receivedMessageTimes.keys.filter { it < cutoff }.forEach(receivedMessageTimes::remove)
        messagesPerMinute = receivedMessageTimes.values.sum()
    }

    fun messagesReceivedWithin(durationMillis: Long): List<ChatMessage> {
        val cutoff = currentTimeMillis() - durationMillis
        return messages.filter { (receivedAtByMessageId[it.id] ?: Long.MIN_VALUE) >= cutoff }
    }

    companion object {
        internal const val MAX_CHAT_MESSAGES = 1_000
    }
}
