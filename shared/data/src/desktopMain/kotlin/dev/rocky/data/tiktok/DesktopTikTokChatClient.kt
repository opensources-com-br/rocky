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

    private fun startAttempt(run: Long, username: String) {
        if (!isCurrent(run) || !active) return
        val token = ++attempt
        pending = worker.schedule({
            if (valid(run, token)) retry(run, username, "A conexão com o TikTok excedeu o tempo de espera.")
        }, connectionTimeoutMillis, TimeUnit.MILLISECONDS)
        runCatching {
            transport = transportFactory.create(username) { event ->
                dispatch { if (valid(run, token)) receive(run, username, event) }
            }
            if (!valid(run, token)) {
                cleanup()
                return
            }
            transport?.connect()
        }.onFailure { retry(run, username, "Não foi possível abrir a conexão com o TikTok.") }
    }

    private fun receive(run: Long, username: String, event: TikTokTransportEvent) {
        when (event) {
            TikTokTransportEvent.Reconnecting ->
                emit(TikTokConnectionPhase.Reconnecting, "Reconectando ao chat do TikTok")
            is TikTokTransportEvent.Connected -> {
                pending?.cancel(false)
                pending = null
                retries = 0
                if (roomId != null && roomId != event.room.id) seenMessageIds.clear()
                roomId = event.room.id
                val room = event.room
                listener.onEvent(TikTokConnectionEvent.Connected(
                    TikTokAccount(room.username, room.displayName), TikTokLiveRoom(room.id, room.title)))
                if (isCurrent(run) && active) listener.onEvent(TikTokConnectionEvent.AudienceUpdated(room.viewerCount))
            }
            is TikTokTransportEvent.AudienceUpdated ->
                listener.onEvent(TikTokConnectionEvent.AudienceUpdated(event.viewerCount))
            is TikTokTransportEvent.CommentReceived -> receiveComment(event.comment)
            is TikTokTransportEvent.Disconnected ->
                retry(run, username, "A conexão com o chat do TikTok foi interrompida.")
            is TikTokTransportEvent.Failed ->
                if (event.retryable) retry(run, username, event.message) else finish(event.message)
            TikTokTransportEvent.LiveEnded -> finish("A live do TikTok foi encerrada.")
        }
    }

    private fun retry(run: Long, username: String, reason: String) {
        cleanup()
        if (!isCurrent(run) || !active) return
        val delay = retryDelaysMillis.getOrNull(retries)
        if (delay == null) {
            finish("$reason Tentativas esgotadas; confira a live e tente conectar novamente.")
            return
        }
        retries++
        emit(TikTokConnectionPhase.Reconnecting,
            "$reason Nova tentativa $retries/${retryDelaysMillis.size} em ${delay / 1_000} s.")
        pending = worker.schedule({ startAttempt(run, username) }, delay, TimeUnit.MILLISECONDS)
    }

    private fun receiveComment(comment: TikTokTransportComment) {
        val id = comment.id.takeUnless { it.isBlank() || it == "0" } ?: "local-${++fallbackMessageId}"
        if (!seenMessageIds.add(id)) return
        while (seenMessageIds.size > MAX_SEEN_MESSAGES) seenMessageIds.remove(seenMessageIds.first())
        listener.onEvent(TikTokConnectionEvent.MessageReceived(ChatMessage(
            id = id, author = comment.author, text = comment.text, platform = StreamPlatform.TikTok,
            authorId = comment.authorId.ifBlank { null }, channelId = comment.roomId.ifBlank { null },
            sourceTimestamp = comment.timestamp)))
    }

    override fun disconnect() {
        val run = generation.incrementAndGet()
        dispatch {
            if (!isCurrent(run)) return@dispatch
            active = false
            cleanup()
            seenMessageIds.clear()
            emit(TikTokConnectionPhase.Disconnected)
        }
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        generation.incrementAndGet()
        dispatch { active = false; cleanup(); seenMessageIds.clear() }
        worker.shutdown()
    }

    private fun finish(message: String) {
        active = false
        cleanup()
        emit(TikTokConnectionPhase.Failed, message)
    }

    private fun cleanup() {
        // Invalidate callbacks before disconnect(), including synchronous disconnect events.
        attempt++
        pending?.cancel(false)
        pending = null
        val previous = transport
        transport = null
        runCatching { previous?.disconnect() }
    }

    private fun isCurrent(run: Long) = !closed.get() && generation.get() == run
    private fun valid(run: Long, token: Long) = isCurrent(run) && active && attempt == token
    private fun emit(phase: TikTokConnectionPhase, detail: String? = null) =
        listener.onEvent(TikTokConnectionEvent.PhaseChanged(phase, detail))

    private fun dispatch(action: () -> Unit) {
        try { worker.execute(action) } catch (_: RejectedExecutionException) {
            // Late library callbacks after close are deliberately ignored.
        }
    }

    private companion object { const val MAX_SEEN_MESSAGES = 1_000 }
}
