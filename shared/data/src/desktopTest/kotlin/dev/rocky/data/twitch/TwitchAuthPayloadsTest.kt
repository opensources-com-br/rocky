package dev.rocky.data.twitch

import kotlin.test.Test
import kotlin.test.assertEquals

class TwitchAuthPayloadsTest {
    @Test
    fun parsesDeviceAuthorizationAndTokens() {
        val authorization = TwitchAuthPayloads.deviceAuthorization(
            """
            {
              "device_code": "device-code",
              "user_code": "ABCD1234",
              "verification_uri": "https://www.twitch.tv/activate",
              "expires_in": 1800,
              "interval": 5
            }
            """.trimIndent(),
        )
        val tokens = TwitchAuthPayloads.tokens(
            """
            {
              "access_token": "access-token",
              "refresh_token": "refresh-token",
              "expires_in": 14400
            }
            """.trimIndent(),
        )

        assertEquals("ABCD1234", authorization.userCode)
        assertEquals(5, authorization.intervalSeconds)
        assertEquals("access-token", tokens.accessToken)
        assertEquals("refresh-token", tokens.refreshToken)
    }

    @Test
    fun parsesValidatedAccount() {
        val account = TwitchAuthPayloads.account(
            """
            {
              "client_id": "client-id",
              "login": "rocky_streamer",
              "user_id": "123456",
              "scopes": ["user:read:chat"]
            }
            """.trimIndent(),
        )

        assertEquals("123456", account.userId)
        assertEquals("rocky_streamer", account.login)
    }

    @Test
    fun parsesLiveViewerCountAndOfflineChannel() {
        assertEquals(
            321,
            TwitchAuthPayloads.viewerCount("""{"data":[{"viewer_count":321}]}"""),
        )
        assertEquals(0, TwitchAuthPayloads.viewerCount("""{"data":[]}"""))
    }
}
