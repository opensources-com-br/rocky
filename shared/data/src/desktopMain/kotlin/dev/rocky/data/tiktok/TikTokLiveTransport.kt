package dev.rocky.data.tiktok

import io.github.jwdeveloper.tiktok.TikTokLive
import io.github.jwdeveloper.tiktok.live.LiveClient
import java.time.Duration
import java.time.Instant
import java.util.logging.Level

internal data class TikTokTransportRoom(
    val id: String,
    val title: String,
    val username: String,
    val displayName: String,
    val viewerCount: Int,
)

internal data class TikTokTransportComment(
    val id: String,
    val authorId: String,
    val author: String,
    val text: String,
    val roomId: String,
    val timestamp: String?,
)

internal sealed interface TikTokTransportEvent {
    data object Reconnecting : TikTokTransportEvent
    data class Connected(val room: TikTokTransportRoom) : TikTokTransportEvent
    data class AudienceUpdated(val viewerCount: Int) : TikTokTransportEvent
    data class CommentReceived(val comment: TikTokTransportComment) : TikTokTransportEvent
    data class Disconnected(val reason: String?) : TikTokTransportEvent
    data class Failed(val message: String) : TikTokTransportEvent
}

internal fun interface TikTokLiveTransportFactory {
    fun create(username: String, onEvent: (TikTokTransportEvent) -> Unit): TikTokLiveTransport
}

internal interface TikTokLiveTransport {
    fun connect()
    fun disconnect()
}

internal class JwTikTokLiveTransport(
    username: String,
    private val onEvent: (TikTokTransportEvent) -> Unit,
) : TikTokLiveTransport {
    private val client: LiveClient = TikTokLive.newClient(username)
        .configure { settings ->
            settings.clientLanguage = "pt"
            settings.logLevel = Level.OFF
            settings.isPrintToConsole = false
            settings.isRetryOnConnectionFailure = true
            settings.retryConnectionTimeout = Duration.ofSeconds(5)
            settings.isFetchGifts = false
        }
        .onConnected { liveClient, _ ->
            val room = liveClient.roomInfo
            onEvent(TikTokTransportEvent.Connected(TikTokTransportRoom(
                id = room.roomId,
                title = room.title.orEmpty().ifBlank { "Live do TikTok" },
                username = room.hostName,
                displayName = room.host?.profileName.orEmpty().ifBlank { room.hostName },
                viewerCount = room.viewersCount.coerceAtLeast(0),
            )))
        }
        .onRoomInfo { _, event ->
            onEvent(TikTokTransportEvent.AudienceUpdated(event.roomInfo.viewersCount.coerceAtLeast(0)))
        }
        .onComment { _, event ->
            val comment = event.text?.trim().orEmpty()
            if (comment.isNotEmpty()) onEvent(TikTokTransportEvent.CommentReceived(TikTokTransportComment(
                id = event.messageId.toString(),
                authorId = event.user.id?.toString().orEmpty(),
                author = event.user.profileName.orEmpty().ifBlank { event.user.name.orEmpty().ifBlank { "TikTok" } },
                text = comment,
                roomId = event.roomId.toString(),
                timestamp = event.timeStamp.takeIf { it > 0 }?.let { Instant.ofEpochMilli(it).toString() },
            )))
        }
        .onReconnecting { _, _ -> onEvent(TikTokTransportEvent.Reconnecting) }
        .onDisconnected { _, event -> onEvent(TikTokTransportEvent.Disconnected(event.reason)) }
        .onError { _, event ->
            onEvent(TikTokTransportEvent.Failed(
                event.exception.message?.takeIf(String::isNotBlank) ?: "Falha na conexão com o TikTok.",
            ))
        }
        .build()

    override fun connect() {
        client.connectAsync().exceptionally { error ->
            onEvent(TikTokTransportEvent.Failed(
                error.cause?.message ?: error.message ?: "Falha na conexão com o TikTok.",
            ))
            null
        }
    }

    override fun disconnect() = client.disconnect()
}
