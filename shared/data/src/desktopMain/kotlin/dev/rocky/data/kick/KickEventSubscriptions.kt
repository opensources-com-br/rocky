package dev.rocky.data.kick

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class KickEventSubscriptions(
    private val httpClient: HttpClient = HttpClient.newHttpClient(),
) {
    fun subscribeToChat(accessToken: String): List<String> {
        val response = send(
            accessToken = accessToken,
            request = HttpRequest.newBuilder(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    """{"events":[{"name":"chat.message.sent","version":1}],"method":"webhook"}""",
                )),
        )
        return KickPayloads.subscriptionIds(response).also { ids ->
            check(ids.isNotEmpty()) {
                KickPayloads.subscriptionError(response) ?: "A Kick não criou a assinatura do chat."
            }
        }
    }

    fun unsubscribe(accessToken: String, subscriptionIds: List<String>) {
        if (subscriptionIds.isEmpty()) return
        val query = subscriptionIds.joinToString("&") { "id=${it.urlEncode()}" }
        send(accessToken, HttpRequest.newBuilder(URI.create("$ENDPOINT?$query")).DELETE())
    }

    private fun send(accessToken: String, request: HttpRequest.Builder): String {
        val response = httpClient.send(
            request.timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer $accessToken").build(),
            HttpResponse.BodyHandlers.ofString(),
        )
        if (response.statusCode() !in 200..299) {
            throw KickApiException(response.statusCode(), KickPayloads.error(response.body()))
        }
        return response.body()
    }

    private companion object {
        const val ENDPOINT = "https://api.kick.com/public/v1/events/subscriptions"
    }
}
