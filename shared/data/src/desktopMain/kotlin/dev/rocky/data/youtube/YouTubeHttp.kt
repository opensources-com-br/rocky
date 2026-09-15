package dev.rocky.data.youtube

import java.net.http.HttpResponse

internal fun HttpResponse<String>.requireYouTubeSuccess(): HttpResponse<String> {
    if (statusCode() !in 200..299) {
        val retryAfter = headers().firstValue("Retry-After").orElse("")
            .toLongOrNull()?.coerceIn(0, 300)?.times(1_000) ?: 0
        throw YouTubeApiException(statusCode(), YouTubePayloads.error(body()),
            YouTubePayloads.errorReasons(body()), retryAfter)
    }
    return this
}

internal class YouTubeApiException(
    val statusCode: Int,
    @Suppress("UNUSED_PARAMETER") youtubeMessage: String?,
    val reasons: Set<String> = emptySet(),
    val retryAfterMillis: Long = 0,
) : Exception(when {
    "liveChatEnded" in reasons -> "A live do YouTube foi encerrada."
    "liveChatDisabled" in reasons -> "O chat desta live do YouTube está desativado."
    reasons.any { it == "quotaExceeded" || it == "dailyLimitExceeded" } ->
        "A cota da API do YouTube foi esgotada. Aguarde a renovação da cota."
    statusCode == 401 -> "Reconecte o YouTube para renovar a autorização."
    else -> "Não foi possível consultar o YouTube (HTTP $statusCode). Verifique a conexão e a autorização."
})
