package dev.rocky.data.youtube

import java.net.http.HttpResponse

internal fun HttpResponse<String>.requireYouTubeSuccess(): HttpResponse<String> {
    if (statusCode() !in 200..299) {
        throw YouTubeApiException(statusCode(), YouTubePayloads.error(body()))
    }
    return this
}

internal class YouTubeApiException(val statusCode: Int, youtubeMessage: String?) :
    Exception(youtubeMessage ?: "YouTube request failed with HTTP $statusCode")
