package dev.rocky.ui.window

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.PulseSample
import dev.rocky.core.youtube.YouTubeAccount
import dev.rocky.core.youtube.YouTubeBroadcast
import dev.rocky.core.youtube.YouTubeChatClient
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionEvent
import dev.rocky.core.youtube.YouTubeConnectionPhase

internal class YouTubeLiveState(
    private val client: YouTubeChatClient,
    private val currentTimeMillis: () -> Long,
) {
    val messages = mutableStateListOf<ChatMessage>()
    val pulse = mutableStateListOf<PulseSample>()
    private val receivedAtByMessageId = mutableStateMapOf<String, Long>()
    private val receivedMessageTimes = mutableStateMapOf<Long, Int>()
    var phase by mutableStateOf(YouTubeConnectionPhase.Disconnected); private set
    var detail by mutableStateOf<String?>(null); private set
    var authorizationUri by mutableStateOf<String?>(null); private set
    var account by mutableStateOf<YouTubeAccount?>(null); private set
    var broadcast by mutableStateOf<YouTubeBroadcast?>(null); private set
    var viewerCount by mutableStateOf<Int?>(null); private set
    var totalMessages by mutableStateOf(0); private set
    var messagesPerMinute by mutableStateOf(0); private set
    var startedAtMillis by mutableStateOf<Long?>(null); private set
    val isConnected get() = phase == YouTubeConnectionPhase.Connected
    val isActive get() = phase != YouTubeConnectionPhase.Disconnected
    val sessionId get() = broadcast?.let { "youtube-${it.id}-${startedAtMillis ?: 0}" }.orEmpty()

    fun connect(configuration: YouTubeConfiguration) {
        if (configuration.clientId.isBlank() || configuration.clientSecret.isBlank()) {
            phase = YouTubeConnectionPhase.Failed
            detail = "Informe Client ID e Client Secret do YouTube."
            return
        }
        messages.clear()
        pulse.clear()
        receivedAtByMessageId.clear()
        receivedMessageTimes.clear()
        totalMessages = 0
        messagesPerMinute = 0
        clearConnection()
        client.connect(configuration, ::receive)
    }

    fun disconnect() {
        client.disconnect()
        clearConnection()
    }

    private fun receive(event: YouTubeConnectionEvent) = Snapshot.withMutableSnapshot {
        when (event) {
            is YouTubeConnectionEvent.PhaseChanged -> {
                phase = event.phase
                detail = event.detail
                if (event.phase == YouTubeConnectionPhase.Disconnected) clearConnection()
                if (event.phase != YouTubeConnectionPhase.Connected) viewerCount = null
            }
            is YouTubeConnectionEvent.AuthorizationRequired -> authorizationUri = event.authorizationUri
            is YouTubeConnectionEvent.Connected -> {
                if (startedAtMillis == null) startedAtMillis = currentTimeMillis()
                phase = YouTubeConnectionPhase.Connected
                account = event.account
                broadcast = event.broadcast
                authorizationUri = null
                detail = "Recebendo o chat de ${event.account.displayName}"
            }
            is YouTubeConnectionEvent.AudienceUpdated -> viewerCount = event.viewerCount
            is YouTubeConnectionEvent.MessageReceived -> receiveMessage(event.message)
        }
    }

    private fun receiveMessage(message: ChatMessage) {
        if (messages.size == MAX_CHAT_MESSAGES) receivedAtByMessageId.remove(messages.removeAt(0).id)
        val receivedAt = currentTimeMillis()
        messages += message.copy(receivedAtMillis = receivedAt, sessionId = sessionId)
        receivedAtByMessageId[message.id] = receivedAt
        totalMessages += 1
        val second = receivedAt / 1_000
        receivedMessageTimes[second] = (receivedMessageTimes[second] ?: 0) + 1
        refreshMetrics()
    }

    fun messagesReceivedWithin(durationMillis: Long): List<ChatMessage> {
        val cutoff = currentTimeMillis() - durationMillis
        return messages.filter { (receivedAtByMessageId[it.id] ?: Long.MIN_VALUE) >= cutoff }
    }

    fun refreshMetrics() = Snapshot.withMutableSnapshot {
        val cutoff = currentTimeMillis() / 1_000 - 59
        receivedMessageTimes.keys.filter { it < cutoff }.forEach(receivedMessageTimes::remove)
        messagesPerMinute = receivedMessageTimes.values.sum()
        if (startedAtMillis != null && isActive) {
            val time = currentTimeMillis() / 5_000 * 5_000
            val sample = PulseSample(time, messagesPerMinute, viewerCount, isConnected)
            if (pulse.lastOrNull()?.timeMillis == time) pulse[pulse.lastIndex] = sample else pulse.add(sample)
            while (pulse.size > 120) pulse.removeAt(0)
        }
    }

    private fun clearConnection() {
        phase = YouTubeConnectionPhase.Disconnected
        detail = null
        authorizationUri = null
        account = null
        broadcast = null
        viewerCount = null
        startedAtMillis = null
    }

    companion object { const val MAX_CHAT_MESSAGES = 1_000 }
}
