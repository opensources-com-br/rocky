package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import dev.rocky.core.facebook.FacebookChatClient
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionEvent
import dev.rocky.core.facebook.FacebookConnectionPhase
import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.facebook.FacebookPage
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.PulseSample

internal class FacebookLiveState(
    private val client: FacebookChatClient,
    private val currentTimeMillis: () -> Long,
) {
    val messages = mutableStateListOf<ChatMessage>()
    val pulse = mutableStateListOf<PulseSample>()
    private val receivedAtByMessageId = mutableStateMapOf<String, Long>()
    private val receivedMessageTimes = mutableStateMapOf<Long, Int>()
    var phase by mutableStateOf(FacebookConnectionPhase.Disconnected); private set
    var detail by mutableStateOf<String?>(null); private set
    var authorizationUri by mutableStateOf<String?>(null); private set
    var page by mutableStateOf<FacebookPage?>(null); private set
    var liveVideo by mutableStateOf<FacebookLiveVideo?>(null); private set
    var viewerCount by mutableStateOf<Int?>(null); private set
    var totalMessages by mutableStateOf(0); private set
    var messagesPerMinute by mutableStateOf(0); private set
    var startedAtMillis by mutableStateOf<Long?>(null); private set
    val isConnected get() = phase == FacebookConnectionPhase.Connected
    val isActive get() = phase !in setOf(FacebookConnectionPhase.Disconnected, FacebookConnectionPhase.Failed)
    val sessionId get() = liveVideo?.let { "facebook-${it.id}-${startedAtMillis ?: 0}" }.orEmpty()

    fun connect(configuration: FacebookConfiguration) {
        if (configuration.appId.isBlank() || configuration.appSecret.isBlank()) {
            phase = FacebookConnectionPhase.Failed
            detail = "Informe App ID e App Secret do Facebook."
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

    private fun receive(event: FacebookConnectionEvent) = Snapshot.withMutableSnapshot {
        when (event) {
            is FacebookConnectionEvent.PhaseChanged -> {
                phase = event.phase
                detail = event.detail
                if (event.phase == FacebookConnectionPhase.Disconnected) clearConnection()
                if (event.phase != FacebookConnectionPhase.Connected) viewerCount = null
            }
            is FacebookConnectionEvent.AuthorizationRequired -> authorizationUri = event.authorizationUri
            is FacebookConnectionEvent.Connected -> {
                if (startedAtMillis == null) startedAtMillis = currentTimeMillis()
                phase = FacebookConnectionPhase.Connected
                page = event.page
                liveVideo = event.liveVideo
                authorizationUri = null
                detail = "Recebendo o chat de ${event.page.name}"
            }
            is FacebookConnectionEvent.AudienceUpdated -> viewerCount = event.viewerCount
            is FacebookConnectionEvent.MessageReceived -> receiveMessage(event.message)
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
        phase = FacebookConnectionPhase.Disconnected
        detail = null
        authorizationUri = null
        page = null
        liveVideo = null
        viewerCount = null
        startedAtMillis = null
    }

    companion object { const val MAX_CHAT_MESSAGES = 1_000 }
}
