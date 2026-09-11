package dev.rocky.data.twitch

import java.net.UnknownHostException
import java.net.http.HttpTimeoutException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TwitchErrorMessageTest {
    @Test
    fun explainsNetworkFailures() {
        assertContains(
            UnknownHostException("id.twitch.tv").twitchUserMessage("Falha"),
            "internet",
        )
        assertContains(
            HttpTimeoutException("timeout").twitchUserMessage("Falha"),
            "demorou",
        )
    }

    @Test
    fun preservesTwitchApiMessages() {
        assertEquals(
            "invalid client id",
            TwitchApiException(400, "invalid client id").twitchUserMessage("Falha"),
        )
    }

    @Test
    fun detectsIncompleteRuntimeThroughWrappedErrors() {
        val error = IllegalStateException("wrapped", NoClassDefFoundError("java/net/http/HttpClient"))
        assertContains(error.twitchUserMessage("Falha"), "versão mais recente")
    }
}
