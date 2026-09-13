package dev.rocky.data.facebook

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64

internal data class FacebookAuthorization(val state: String, val uri: String)

internal fun createFacebookAuthorization(
    appId: String,
    redirectUri: String,
    random: SecureRandom = SecureRandom(),
): FacebookAuthorization {
    val state = ByteArray(24).also(random::nextBytes).let {
        Base64.getUrlEncoder().withoutPadding().encodeToString(it)
    }
    val parameters = linkedMapOf(
        "client_id" to appId,
        "redirect_uri" to redirectUri,
        "response_type" to "code",
        "scope" to "pages_show_list,pages_read_engagement,pages_read_user_content",
        "state" to state,
    )
    val query = parameters.entries.joinToString("&") { (key, value) ->
        "${key.facebookUrlEncode()}=${value.facebookUrlEncode()}"
    }
    return FacebookAuthorization(state, "https://www.facebook.com/v25.0/dialog/oauth?$query")
}

internal fun String.facebookUrlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
