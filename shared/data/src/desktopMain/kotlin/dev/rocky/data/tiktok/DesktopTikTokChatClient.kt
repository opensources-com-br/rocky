package dev.rocky.data.tiktok

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.tiktok.TikTokAccount
import dev.rocky.core.tiktok.TikTokChatClient
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.core.tiktok.TikTokConnectionEvent
import dev.rocky.core.tiktok.TikTokConnectionListener
import dev.rocky.core.tiktok.TikTokConnectionPhase
import dev.rocky.core.tiktok.TikTokLiveRoom
import java.util.LinkedHashSet
import java.util.concurrent.atomic.AtomicLong

class DesktopTikTokChatClient internal constructor(
    private val transportFactory: TikTokLiveTransportFactory,
) : TikTokChatClient {
    constructor() : this(TikTokLiveTransportFactory(::JwTikTokLiveTransport))

    private val generation = AtomicLong()
    private val fallbackMessageId = AtomicLong()
    private val seenMessageIds = LinkedHashSet<String>()
    @Volatile private var active = false
    @Volatile private var listener = TikTokConnectionListener {}
    @Volatile private var transport: TikTokLiveTransport? = null

    override fun connect(configuration: TikTokConfiguration, listener: TikTokConnectionListener) {
        stop(notify = false)
        val username = configuration.username.trim().removePrefix("@").trim()
        require(username.isNotBlank()) { "Informe o @usuário do TikTok." }
        val run = generation.incrementAndGet()
        this.listener = listener
        active = true
        seenMessageIds.clear()
        emit(TikTokConnectionPhase.Connecting, "Procurando a live de @$username")
        runCatching {
            transportFactory.create(username) { event -> receive(run, event) }.also {
                transport = it
                it.connect()
            }
        }.onFailure { fail(run, it.userMessage()) }
    }

    private fun receive(run: Long, event: TikTokTransportEvent) {
        if (!isCurrent(run)) return
        when (event) {
            TikTokTransportEvent.Reconnecting ->
                emit(TikTokConnectionPhase.Reconnecting, "Reconectando ao chat do TikTok")
            is TikTokTransportEvent.Connected -> {
                val room = event.room
                listener.onEvent(TikTokConnectionEvent.Connected(
                    TikTokAccount(room.username, room.displayName),
                    TikTokLiveRoom(room.id, room.title),
                ))
                listener.onEvent(TikTokConnectionEvent.AudienceUpdated(room.viewerCount))
            }
            is TikTokTransportEvent.AudienceUpdated ->
                listener.onEvent(TikTokConnectionEvent.AudienceUpdated(event.viewerCount))
            is TikTokTransportEvent.CommentReceived -> receiveComment(event.comment)
            is TikTokTransportEvent.Disconnected ->
                fail(run, event.reason?.takeIf(String::isNotBlank) ?: "O chat do TikTok foi desconectado.")
            is TikTokTransportEvent.Failed -> fail(run, event.message)
        }
    }

    private fun receiveComment(comment: TikTokTransportComment) {
        val id = comment.id.takeUnless { it.isBlank() || it == "0" }
            ?: "local-${fallbackMessageId.incrementAndGet()}"
        if (!remember(id)) return
        listener.onEvent(TikTokConnectionEvent.MessageReceived(ChatMessage(
            id = id,
            author = comment.author,
            text = comment.text,
            platform = StreamPlatform.TikTok,
            authorId = comment.authorId.ifBlank { null },
            channelId = comment.roomId.ifBlank { null },
            sourceTimestamp = comment.timestamp,
        )))
    }

    private fun remember(id: String): Boolean = synchronized(seenMessageIds) {
        if (!seenMessageIds.add(id)) return@synchronized false
        while (seenMessageIds.size > MAX_SEEN_MESSAGES) seenMessageIds.remove(seenMessageIds.first())
        true
    }

    override fun disconnect() = stop(notify = true)

    override fun close() = stop(notify = false)

    private fun stop(notify: Boolean) {
        active = false
        generation.incrementAndGet()
        transport?.disconnect()
        transport = null
        seenMessageIds.clear()
        if (notify) emit(TikTokConnectionPhase.Disconnected)
    }

    private fun emit(phase: TikTokConnectionPhase, detail: String? = null) =
        listener.onEvent(TikTokConnectionEvent.PhaseChanged(phase, detail))

    private fun dispatch(action: () -> Unit) {
        try { worker.execute(action) } catch (_: RejectedExecutionException) {
            // Late library callbacks after close are deliberately ignored.
        }
    }

    private companion object { const val MAX_SEEN_MESSAGES = 1_000 }
}
