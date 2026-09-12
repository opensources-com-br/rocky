package dev.rocky.data.youtube

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

internal class YouTubeAuthorizationReceiver(
    redirectUri: String,
    private val expectedState: String,
    private val onCode: (String) -> Unit,
) : AutoCloseable {
    private val redirect = URI.create(redirectUri)
    private val executor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-youtube-callback").apply { isDaemon = true }
    }
    private val server: HttpServer

    init {
        require(redirect.scheme == "http" && redirect.host in setOf("localhost", "127.0.0.1")) {
            "O callback do YouTube deve usar um endereço local."
        }
        require(redirect.port in 1..65535) { "O callback do YouTube precisa informar uma porta." }
        server = HttpServer.create(InetSocketAddress(redirect.host, redirect.port), 0)
        server.executor = executor
        server.createContext(redirect.path, ::handleCallback)
        server.start()
    }

    private fun handleCallback(exchange: HttpExchange) {
        if (exchange.requestMethod != "GET") return exchange.respond(405, "Método não permitido")
        val query = parseQuery(exchange.requestURI.rawQuery.orEmpty())
        if (query["state"] != expectedState || query["code"].isNullOrBlank()) {
            return exchange.respond(400, "Autorização inválida. Volte ao Rocky e tente novamente.")
        }
        exchange.respond(200, "YouTube conectado. Você pode fechar esta janela e voltar ao Rocky.")
        onCode(query.getValue("code"))
    }

    override fun close() {
        server.stop(0)
        executor.shutdownNow()
    }

    private fun HttpExchange.respond(status: Int, text: String) {
        val bytes = text.toByteArray(StandardCharsets.UTF_8)
        responseHeaders.set("Content-Type", "text/plain; charset=utf-8")
        sendResponseHeaders(status, bytes.size.toLong())
        responseBody.use { it.write(bytes) }
    }

    private fun parseQuery(query: String): Map<String, String> = query.split("&")
        .mapNotNull { it.split("=", limit = 2).takeIf { parts -> parts.size == 2 } }
        .associate { (key, value) ->
            URLDecoder.decode(key, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8)
        }
}
