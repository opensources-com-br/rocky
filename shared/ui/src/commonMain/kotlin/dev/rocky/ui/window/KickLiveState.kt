package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import dev.rocky.core.kick.KickAccount
import dev.rocky.core.kick.KickChatClient
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionPhase
import dev.rocky.core.live.ChatMessage

internal class KickLiveState(
    private val client: KickChatClient,
    private val currentTimeMillis: () -> Long,
) {
    val messages = mutableStateListOf<ChatMessage>()
    private val receivedMessageTimes = mutableStateMapOf<Long, Int>()
    var phase by mutableStateOf(KickConnectionPhase.Disconnected); private set
    var detail by mutableStateOf<String?>(null); private set
    var authorizationUri by mutableStateOf<String?>(null); private set
    var account by mutableStateOf<KickAccount?>(null); private set
    var viewerCount by mutableStateOf<Int?>(null); private set
    var totalMessages by mutableStateOf(0); private set
    var messagesPerMinute by mutableStateOf(0); private set
    var startedAtMillis by mutableStateOf<Long?>(null); private set
    val isConnected get() = phase == KickConnectionPhase.Connected
    val isActive get() = phase != KickConnectionPhase.Disconnected
    val sessionId get() = account?.let { "kick-${it.userId}-${startedAtMillis ?: 0}" }.orEmpty()

    fun connect(configuration: KickConfiguration) {
        if (configuration.clientId.isBlank() || configuration.clientSecret.isBlank()) {
            phase = KickConnectionPhase.Failed
            detail = "Informe Client ID e Client Secret da Kick."
            return
        }
        client.connect(configuration, ::receive)
    }

    fun disconnect() {
        client.disconnect()
        clearConnection()
    }

    private fun receive(event: KickConnectionEvent) = Snapshot.withMutableSnapshot {
        when (event) {
            is KickConnectionEvent.PhaseChanged -> {
                phase = event.phase
                detail = event.detail
                if (event.phase == KickConnectionPhase.Disconnected) clearConnection()
                if (event.phase != KickConnectionPhase.Connected) viewerCount = null
            }
            is KickConnectionEvent.AuthorizationRequired -> authorizationUri = event.authorizationUri
            is KickConnectionEvent.Connected -> {
                if (startedAtMillis == null) startedAtMillis = currentTimeMillis()
                phase = KickConnectionPhase.Connected
                account = event.account
                authorizationUri = null
                detail = "Recebendo o chat de @${event.account.username}"
            }
            is KickConnectionEvent.AudienceUpdated -> viewerCount = event.viewerCount
            is KickConnectionEvent.MessageReceived -> {
                if (messages.size == MAX_CHAT_MESSAGES) {
                    messages.removeAt(0)
                }
                val receivedAt = currentTimeMillis()
                messages += event.message.copy(receivedAtMillis = receivedAt, sessionId = sessionId)
                totalMessages += 1
                val second = receivedAt / 1_000
                receivedMessageTimes[second] = (receivedMessageTimes[second] ?: 0) + 1
                refreshMetrics()
            }
        }
    }

    fun refreshMetrics() = Snapshot.withMutableSnapshot {
        val cutoff = currentTimeMillis() / 1_000 - 59
        receivedMessageTimes.keys.filter { it < cutoff }.forEach(receivedMessageTimes::remove)
        messagesPerMinute = receivedMessageTimes.values.sum()
    }

    private fun clearConnection() {
        phase = KickConnectionPhase.Disconnected
        detail = null
        authorizationUri = null
        account = null
        viewerCount = null
        startedAtMillis = null
    }

    companion object { const val MAX_CHAT_MESSAGES = 1_000 }
}
