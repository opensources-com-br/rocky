package dev.rocky.data.facebook

import dev.rocky.core.live.StreamPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FacebookPayloadsTest {
    @Test fun parsesPagesAndActiveLiveVideo() {
        val pages = FacebookPayloads.pages(
            """{"data":[{"id":"page-1","name":"Canal Rocky","access_token":"page-token"}]}""",
        )
        assertEquals("Canal Rocky", pages.single().page.name)
        assertEquals("page-token", pages.single().accessToken)
        assertEquals(
            "live-1",
            FacebookPayloads.liveVideo("""{"data":[{"id":"live-1","title":"Minha live"}]}""")?.id,
        )
        assertNull(FacebookPayloads.liveVideo("""{"data":[]}"""))
    }

    @Test fun parsesCommentsInResponseOrder() {
        val page = FacebookPayloads.comments(
            """{
              "data":[
                {"id":"c1","message":"Primeira","created_time":"2026-09-13T10:00:00+0000","from":{"id":"u1","name":"Ana"}},
                {"id":"c2","message":"Segunda","created_time":"2026-09-13T10:00:01+0000","from":{"id":"u2","name":"Bia"}}
              ],
              "paging":{"cursors":{"after":"next"}}
            }""",
        )
        assertEquals(listOf("c1", "c2"), page.messages.map { it.id })
        assertEquals(StreamPlatform.Facebook, page.messages.last().platform)
        assertEquals("next", page.after)
    }

    @Test fun parsesTokenAudienceAndErrors() {
        assertEquals("access", FacebookPayloads.accessToken("""{"access_token":"access"}"""))
        assertEquals(87, FacebookPayloads.viewerCount("""{"live_views":87}"""))
        assertEquals("Token inválido", FacebookPayloads.error("""{"error":{"message":"Token inválido"}}"""))
    }
}
