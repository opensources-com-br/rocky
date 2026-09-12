package dev.rocky.data.kick

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import dev.rocky.core.live.ChatMessage
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.LinkedHashSet
import java.util.concurrent.Executors

internal class KickLocalReceiver(
    redirectUri: String,
    private val expectedState: String,
    publicKeyPem: String,
    private val onCode: (String) -> Unit,
    private val onMessage: (ChatMessage) -> Unit,
) : AutoCloseable {
    private val redirect = URI.create(redirectUri)
    private val verifier = KickWebhookVerifier(publicKeyPem)
    private val seen = LinkedHashSet<String>()
    private val executor = Executors.newCachedThreadPool { task ->
        Thread(task, "rocky-kick-callback").apply { isDaemon = true }
    }
    private val server: HttpServer

    init {
        require(redirect.scheme == "http" && redirect.host in setOf("localhost", "127.0.0.1")) {
            "O callback da Kick deve usar http://localhost."
        }
        require(redirect.port in 1..65535) { "O callback da Kick precisa informar uma porta." }
        server = HttpServer.create(InetSocketAddress(redirect.host, redirect.port), 0)
        server.executor = executor
        server.createContext(redirect.path, ::handleCallback)
        server.createContext(WEBHOOK_PATH, ::handleWebhook)
        server.start()
    }

    private fun handleCallback(exchange: HttpExchange) {
        if (exchange.requestMethod != "GET") return exchange.respond(405, "Método não permitido")
        val query = parseQuery(exchange.requestURI.rawQuery.orEmpty())
        if (query["state"] != expectedState || query["code"].isNullOrBlank()) {
            return exchange.respond(400, "Autorização inválida. Volte ao Rocky e tente novamente.")
        }
        exchange.respond(200, "Kick conectada. Você pode fechar esta janela e voltar ao Rocky.")
        onCode(query.getValue("code"))
    }

    private fun handleWebhook(exchange: HttpExchange) {
        if (exchange.requestMethod != "POST") return exchange.respond(405, "Método não permitido")
        val body = exchange.requestBody.use { it.readBytes() }
        val messageId = exchange.requestHeaders.getFirst("Kick-Event-Message-Id").orEmpty()
        val timestamp = exchange.requestHeaders.getFirst("Kick-Event-Message-Timestamp").orEmpty()
        val signature = exchange.requestHeaders.getFirst("Kick-Event-Signature").orEmpty()
        val type = exchange.requestHeaders.getFirst("Kick-Event-Type").orEmpty()
        if (type != "chat.message.sent" || messageId.isBlank() ||
            !verifier.verify(messageId, timestamp, body, signature)
        ) return exchange.respond(401, "Webhook inválido")
        if (!remember(messageId)) return exchange.respond(204, "")
        val message = runCatching { KickPayloads.chatMessage(body.toString(StandardCharsets.UTF_8)) }
            .getOrElse { return exchange.respond(400, "Payload inválido") }
        exchange.respond(204, "")
        onMessage(message)
    }

    @Synchronized
    private fun remember(id: String): Boolean {
        if (!seen.add(id)) return false
        while (seen.size > 1_000) seen.remove(seen.first())
        return true
    }

    override fun close() {
        server.stop(0)
        executor.shutdownNow()
    }

    private fun HttpExchange.respond(status: Int, text: String) {
        val bytes = text.toByteArray(StandardCharsets.UTF_8)
        responseHeaders.set("Content-Type", "text/plain; charset=utf-8")
        sendResponseHeaders(status, if (status == 204) -1 else bytes.size.toLong())
        if (status != 204) responseBody.use { it.write(bytes) } else close()
    }

    private fun parseQuery(query: String): Map<String, String> = query.split("&")
        .mapNotNull { part -> part.split("=", limit = 2).takeIf { it.size == 2 } }
        .associate { (key, value) -> URLDecoder.decode(key, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8) }

    companion object { const val WEBHOOK_PATH = "/webhooks/kick" }
}
