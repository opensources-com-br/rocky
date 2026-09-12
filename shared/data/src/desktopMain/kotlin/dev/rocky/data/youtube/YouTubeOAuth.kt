package dev.rocky.data.youtube

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

internal data class YouTubeAuthorization(
    val state: String,
    val verifier: String,
    val uri: String,
)

internal fun createYouTubeAuthorization(
    clientId: String,
    redirectUri: String,
    random: SecureRandom = SecureRandom(),
): YouTubeAuthorization {
    fun randomUrlSafe(size: Int) = ByteArray(size).also(random::nextBytes).base64Url()
    val state = randomUrlSafe(24)
    val verifier = randomUrlSafe(48)
    val challenge = MessageDigest.getInstance("SHA-256")
        .digest(verifier.toByteArray(StandardCharsets.US_ASCII)).base64Url()
    val parameters = linkedMapOf(
        "client_id" to clientId,
        "redirect_uri" to redirectUri,
        "response_type" to "code",
        "scope" to "https://www.googleapis.com/auth/youtube.readonly",
        "access_type" to "offline",
        "prompt" to "consent",
        "state" to state,
        "code_challenge" to challenge,
        "code_challenge_method" to "S256",
    )
    val query = parameters.entries.joinToString("&") { (key, value) ->
        "${key.youtubeUrlEncode()}=${value.youtubeUrlEncode()}"
    }
    return YouTubeAuthorization(state, verifier, "https://accounts.google.com/o/oauth2/v2/auth?$query")
}

internal fun String.youtubeUrlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
private fun ByteArray.base64Url() = Base64.getUrlEncoder().withoutPadding().encodeToString(this)
