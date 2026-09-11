package dev.rocky.data.twitch

import java.net.ConnectException
import java.net.UnknownHostException
import java.net.http.HttpTimeoutException
import javax.net.ssl.SSLException

internal fun Throwable.twitchUserMessage(fallback: String): String {
    val cause = rootCause()
    return when (cause) {
        is TwitchApiException -> cause.twitchMessage ?: "$fallback (HTTP ${cause.statusCode})"
        is UnknownHostException -> "Não foi possível encontrar a Twitch. Verifique sua conexão com a internet."
        is HttpTimeoutException -> "A Twitch demorou para responder. Tente conectar novamente."
        is ConnectException -> "Não foi possível acessar a Twitch. Verifique sua conexão com a internet."
        is SSLException -> "Não foi possível estabelecer uma conexão segura com a Twitch."
        is NoClassDefFoundError -> "Esta instalação do Rocky está incompleta. Instale a versão mais recente."
        else -> fallback
    }
}

private fun Throwable.rootCause(): Throwable {
    var current = this
    while (current.cause != null && current.cause !== current) current = current.cause!!
    return current
}
