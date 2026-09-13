package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    private fun receive(event: FacebookConnectionEvent) = Unit
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
