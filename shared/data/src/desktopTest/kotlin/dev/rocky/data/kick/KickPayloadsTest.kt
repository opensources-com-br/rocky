package dev.rocky.data.kick

import dev.rocky.core.live.StreamPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KickPayloadsTest {
    @Test fun parsesTokensAndAccount() {
        assertEquals(KickTokens("access", "refresh"), KickPayloads.tokens(
            """{"access_token":"access","refresh_token":"refresh","expires_in":7200}"""))
        assertEquals("rocky_live", KickPayloads.account(
            """{"data":[{"user_id":42,"name":"rocky_live"}],"message":"OK"}""").username)
    }

    @Test fun parsesViewerCountAndOfflineChannel() {
        assertEquals(321, KickPayloads.viewerCount(
            """{"data":[{"stream":{"viewer_count":321}}]}"""))
        assertNull(KickPayloads.viewerCount("""{"data":[{"stream":null}]}"""))
    }

    @Test fun parsesOfficialChatWebhookShape() {
        val message = KickPayloads.chatMessage("""{
            "message_id":"01ABC","broadcaster":{"user_id":42},
            "sender":{"user_id":9,"username":"viewer"},
            "content":"Qual é o preço?","created_at":"2026-09-12T12:00:00Z"
        }""")
        assertEquals("01ABC", message.id)
        assertEquals("viewer", message.author)
        assertEquals("9", message.authorId)
        assertEquals("42", message.channelId)
        assertEquals(StreamPlatform.Kick, message.platform)
    }

    @Test fun parsesCreatedSubscriptionIds() {
        assertEquals(listOf("01SUB"), KickPayloads.subscriptionIds(
            """{"data":[{"name":"chat.message.sent","version":1,"subscription_id":"01SUB"}]}"""))
    }
}
