package dev.rocky.data.facebook

import java.net.URI
import java.net.URLDecoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FacebookOAuthTest {
    @Test fun requestsPageReadPermissionsAndState() {
        val authorization = createFacebookAuthorization(
            "app id",
            "http://127.0.0.1:18183/oauth/facebook/callback",
        )
        val query = URI.create(authorization.uri).rawQuery.split("&").associate {
            val (key, value) = it.split("=", limit = 2)
            URLDecoder.decode(key, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8)
        }
        assertEquals("code", query["response_type"])
        assertEquals("pages_show_list,pages_read_engagement", query["scope"])
        assertEquals(authorization.state, query["state"])
        assertTrue(authorization.state.length >= 32)
    }
}
