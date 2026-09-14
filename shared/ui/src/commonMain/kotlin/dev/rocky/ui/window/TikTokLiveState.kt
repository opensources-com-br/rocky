package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.PulseSample
import dev.rocky.core.tiktok.TikTokAccount
import dev.rocky.core.tiktok.TikTokChatClient
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.core.tiktok.TikTokConnectionEvent
import dev.rocky.core.tiktok.TikTokConnectionPhase
import dev.rocky.core.tiktok.TikTokLiveRoom

internal class TikTokLiveState(
    private val client: TikTokChatClient,
    private val currentTimeMillis: () -> Long,
) {
    val messages = mutableStateListOf<ChatMessage>()
    val pulse = mutableStateListOf<PulseSample>()
    private val receivedAtByMessageId = mutableStateMapOf<String, Long>()
    private val receivedMessageTimes = mutableStateMapOf<Long, Int>()
    var phase by mutableStateOf(TikTokConnectionPhase.Disconnected); private set
    var detail by mutableStateOf<String?>(null); private set
    var account by mutableStateOf<TikTokAccount?>(null); private set
    var room by mutableStateOf<TikTokLiveRoom?>(null); private set
    var viewerCount by mutableStateOf<Int?>(null); private set
    var totalMessages by mutableStateOf(0); private set
    var messagesPerMinute by mutableStateOf(0); private set
    var startedAtMillis by mutableStateOf<Long?>(null); private set
    val isConnected get() = phase == TikTokConnectionPhase.Connected
    val isActive get() = phase !in setOf(TikTokConnectionPhase.Disconnected, TikTokConnectionPhase.Failed)
    val sessionId get() = room?.let { "tiktok-${it.id}-${startedAtMillis ?: 0}" }.orEmpty()

    fun connect(configuration: TikTokConfiguration) {
        if (configuration.username.trim().removePrefix("@").isBlank()) {
            phase = TikTokConnectionPhase.Failed
            detail = "Informe o @usuário do TikTok."
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

    private fun receive(event: TikTokConnectionEvent) = Snapshot.withMutableSnapshot {
        when (event) {
            is TikTokConnectionEvent.PhaseChanged -> {
                phase = event.phase
                detail = event.detail
                if (event.phase == TikTokConnectionPhase.Disconnected) clearConnection()
                if (event.phase != TikTokConnectionPhase.Connected) viewerCount = null
            }
            is TikTokConnectionEvent.Connected -> {
                if (startedAtMillis == null) startedAtMillis = currentTimeMillis()
                phase = TikTokConnectionPhase.Connected
                account = event.account
                room = event.room
                detail = "Recebendo o chat de @${event.account.username}"
            }
            is TikTokConnectionEvent.AudienceUpdated -> viewerCount = event.viewerCount
            is TikTokConnectionEvent.MessageReceived -> receiveMessage(event.message)
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
        phase = TikTokConnectionPhase.Disconnected
        detail = null
        account = null
        room = null
        viewerCount = null
        startedAtMillis = null
    }

    companion object { const val MAX_CHAT_MESSAGES = 1_000 }
}
