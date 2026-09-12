package dev.rocky.data.youtube

import dev.rocky.core.youtube.YouTubeAccount
import dev.rocky.core.youtube.YouTubeBroadcast
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class YouTubeLiveApi(
    private val httpClient: HttpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build(),
    private val apiBase: String = "https://www.googleapis.com/youtube/v3",
) {
    fun account(accessToken: String): YouTubeAccount = YouTubePayloads.account(
        get("/channels?part=id%2Csnippet&mine=true", accessToken),
    )

    fun activeBroadcast(accessToken: String): YouTubeBroadcast? = YouTubePayloads.broadcast(
        get("/liveBroadcasts?part=id%2Csnippet&broadcastStatus=active&mine=true&maxResults=1", accessToken),
    )

    fun viewerCount(accessToken: String, broadcastId: String): Int? = YouTubePayloads.viewerCount(
        get("/videos?part=liveStreamingDetails&id=${broadcastId.youtubeUrlEncode()}", accessToken),
    )

    fun chatPage(accessToken: String, liveChatId: String, pageToken: String?): YouTubeChatPage {
        val page = pageToken?.let { "&pageToken=${it.youtubeUrlEncode()}" }.orEmpty()
        return YouTubePayloads.chatPage(
            get(
                "/liveChat/messages?part=id%2Csnippet%2CauthorDetails&liveChatId=${liveChatId.youtubeUrlEncode()}&maxResults=200$page",
                accessToken,
            ),
        )
    }

    private fun get(path: String, accessToken: String): String {
        val request = HttpRequest.newBuilder(URI.create("$apiBase$path"))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", "Bearer $accessToken")
            .GET()
            .build()
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).requireYouTubeSuccess().body()
    }
}
