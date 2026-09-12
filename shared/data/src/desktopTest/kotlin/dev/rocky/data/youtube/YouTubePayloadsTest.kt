package dev.rocky.data.youtube

import dev.rocky.core.live.StreamPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class YouTubePayloadsTest {
    @Test fun parsesAccountBroadcastAndTokens() {
        assertEquals(
            "Canal Rocky",
            YouTubePayloads.account("""{"items":[{"id":"channel-1","snippet":{"title":"Canal Rocky"}}]}""").displayName,
        )
        val broadcast = YouTubePayloads.broadcast(
            """{"items":[{"id":"video-1","snippet":{"title":"Live","liveChatId":"chat-1"}}]}""",
        )
        assertEquals("chat-1", broadcast?.liveChatId)
        assertNull(YouTubePayloads.broadcast("""{"items":[]}"""))
        val tokens = YouTubePayloads.tokens(
            """{"access_token":"access","refresh_token":"refresh","expires_in":3600}""",
        )
        assertEquals("refresh", tokens.refreshToken)
    }

    @Test fun parsesChatInChronologicalResponseOrder() {
        val page = YouTubePayloads.chatPage(
            """{
              "nextPageToken":"next","pollingIntervalMillis":2500,
              "items":[
                {"id":"m1","snippet":{"displayMessage":"Primeira","publishedAt":"2026-09-12T18:00:00Z"},"authorDetails":{"channelId":"a1","displayName":"Ana"}},
                {"id":"m2","snippet":{"displayMessage":"Segunda","publishedAt":"2026-09-12T18:00:01Z"},"authorDetails":{"channelId":"a2","displayName":"Bia"}}
              ]
            }""",
        )
        assertEquals(listOf("m1", "m2"), page.messages.map { it.id })
        assertEquals(StreamPlatform.YouTube, page.messages.last().platform)
        assertEquals("next", page.nextPageToken)
        assertEquals(2500, page.pollingIntervalMillis)
    }
}
