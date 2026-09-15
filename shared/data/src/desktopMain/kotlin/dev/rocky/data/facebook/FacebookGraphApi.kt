package dev.rocky.data.facebook

import dev.rocky.core.facebook.FacebookLiveVideo
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class FacebookGraphApi(
    private val httpClient: HttpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build(),
    private val apiBase: String = "https://graph.facebook.com/v25.0",
) {
    fun exchangeCode(appId: String, appSecret: String, redirectUri: String, code: String): String {
        val body = linkedMapOf(
            "client_id" to appId,
            "client_secret" to appSecret,
            "redirect_uri" to redirectUri,
            "code" to code,
        ).entries.joinToString("&") { (key, value) ->
            "${key.facebookUrlEncode()}=${value.facebookUrlEncode()}"
        }
        val request = HttpRequest.newBuilder(URI.create("$apiBase/oauth/access_token"))
            .timeout(Duration.ofSeconds(30))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        return FacebookPayloads.accessToken(send(request))
    }

    fun pages(userAccessToken: String): List<FacebookPageAccess> = FacebookPayloads.pages(
        get("/me/accounts?fields=id%2Cname%2Caccess_token&limit=100", userAccessToken),
    )

    fun activeLiveVideo(pageId: String, pageAccessToken: String): FacebookLiveVideo? = FacebookPayloads.liveVideo(
        get("/${pageId.facebookUrlEncode()}/live_videos?broadcast_status=LIVE&fields=id%2Ctitle&limit=1", pageAccessToken),
    )

    fun comments(liveVideoId: String, pageAccessToken: String): FacebookCommentPage {
        return FacebookPayloads.comments(
            get("/${liveVideoId.facebookUrlEncode()}/comments?order=reverse_chronological&filter=stream&fields=id%2Cmessage%2Cfrom%2Ccreated_time&limit=100", pageAccessToken),
        )
    }

    fun viewerCount(liveVideoId: String, pageAccessToken: String): Int? = FacebookPayloads.viewerCount(
        get("/${liveVideoId.facebookUrlEncode()}?fields=live_views", pageAccessToken),
    )

    private fun get(path: String, accessToken: String): String {
        val request = HttpRequest.newBuilder(URI.create("$apiBase$path"))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", "Bearer $accessToken")
            .GET()
            .build()
        return send(request)
    }

    private fun send(request: HttpRequest): String {
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() in 200..299) return response.body()
        val status = response.statusCode()
        val retryable = status !in setOf(401, 403) &&
            (FacebookPayloads.retryableError(response.body()) ?: (status == 429 || status in 500..599))
        val retryAfter = response.headers().firstValue("Retry-After").orElse("")
            .toLongOrNull()?.coerceIn(0, 300)?.times(1_000) ?: 0
        throw FacebookRequestFailure(retryable, retryAfter)
    }
}
