package dev.rocky.data.kick

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

internal data class KickAuthorization(
    val state: String,
    val verifier: String,
    val uri: String,
)

internal fun createKickAuthorization(
    clientId: String,
    redirectUri: String,
    random: SecureRandom = SecureRandom(),
): KickAuthorization {
    fun randomUrlSafe(size: Int) = ByteArray(size).also(random::nextBytes).base64Url()
    val state = randomUrlSafe(24)
    val verifier = randomUrlSafe(48)
    val challenge = MessageDigest.getInstance("SHA-256")
        .digest(verifier.toByteArray(StandardCharsets.US_ASCII)).base64Url()
    val parameters = linkedMapOf(
        "response_type" to "code",
        "client_id" to clientId,
        "redirect_uri" to redirectUri,
        "state" to state,
        "scope" to "user:read channel:read events:subscribe",
        "code_challenge" to challenge,
        "code_challenge_method" to "S256",
    )
    val query = parameters.entries.joinToString("&") { (key, value) ->
        "${key.urlEncode()}=${value.urlEncode()}"
    }
    return KickAuthorization(state, verifier, "https://id.kick.com/oauth/authorize?$query")
}

internal fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
private fun ByteArray.base64Url() = Base64.getUrlEncoder().withoutPadding().encodeToString(this)
