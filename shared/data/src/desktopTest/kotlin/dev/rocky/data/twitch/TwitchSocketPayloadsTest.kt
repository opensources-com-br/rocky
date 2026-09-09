package dev.rocky.data.twitch

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TwitchSocketPayloadsTest {
    @Test
    fun parsesWelcomeAndReconnect() {
        val welcome = TwitchSocketPayloads.parse(
            """
            {
              "metadata": { "message_type": "session_welcome" },
              "payload": {
                "session": { "id": "session-1", "keepalive_timeout_seconds": 10 }
              }
            }
            """.trimIndent(),
        )
        val reconnect = TwitchSocketPayloads.parse(
            """
            {
              "metadata": { "message_type": "session_reconnect" },
              "payload": { "session": { "reconnect_url": "wss://reconnect.example" } }
            }
            """.trimIndent(),
        )

        assertEquals("session-1", assertIs<TwitchSocketEvent.Welcome>(welcome).sessionId)
        assertEquals("wss://reconnect.example", assertIs<TwitchSocketEvent.Reconnect>(reconnect).url)
    }

    @Test
    fun parsesChatMessage() {
        val event = TwitchSocketPayloads.parse(
            """
            {
              "metadata": { "message_type": "notification" },
              "payload": {
                "event": {
                  "message_id": "message-1",
                  "chatter_user_name": "viewer",
                  "message": { "text": "Olá, Rocky!" }
                }
              }
            }
            """.trimIndent(),
        )

        val message = assertIs<TwitchSocketEvent.MessageReceived>(event).message
        assertEquals("message-1", message.id)
        assertEquals("viewer", message.author)
        assertEquals("Olá, Rocky!", message.text)
    }
}
