package dev.rocky.data.kick

import java.net.URI
import java.net.URLDecoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KickOAuthTest {
    @Test fun createsPkceAuthorizationRequest() {
        val authorization = createKickAuthorization("client id", "http://localhost:18181/oauth/kick/callback")
        val query = URI.create(authorization.uri).rawQuery.split("&").associate {
            val (key, value) = it.split("=", limit = 2)
            URLDecoder.decode(key, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8)
        }
        assertEquals("code", query["response_type"])
        assertEquals("client id", query["client_id"])
        assertEquals(authorization.state, query["state"])
        assertEquals("S256", query["code_challenge_method"])
        assertEquals("user:read channel:read events:subscribe", query["scope"])
        assertTrue(authorization.verifier.length >= 43)
    }
}
