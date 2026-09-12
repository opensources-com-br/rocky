package dev.rocky.data.youtube

import java.net.URI
import java.net.URLDecoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YouTubeOAuthTest {
    @Test fun createsDesktopAuthorizationWithOfflineAccess() {
        val authorization = createYouTubeAuthorization(
            "client id",
            "http://127.0.0.1:18182/oauth/youtube/callback",
        )
        val query = URI.create(authorization.uri).rawQuery.split("&").associate {
            val (key, value) = it.split("=", limit = 2)
            URLDecoder.decode(key, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8)
        }
        assertEquals("code", query["response_type"])
        assertEquals("offline", query["access_type"])
        assertEquals("consent", query["prompt"])
        assertEquals("https://www.googleapis.com/auth/youtube.readonly", query["scope"])
        assertEquals("S256", query["code_challenge_method"])
        assertEquals(authorization.state, query["state"])
        assertTrue(authorization.verifier.length >= 43)
    }
}
