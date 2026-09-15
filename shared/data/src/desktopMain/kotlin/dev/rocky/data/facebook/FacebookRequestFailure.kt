package dev.rocky.data.facebook

import java.io.IOException

internal class FacebookRequestFailure(
    val retryable: Boolean,
    val retryAfterMillis: Long = 0,
) : IOException("Não foi possível consultar o Facebook. Verifique a conexão e as permissões da Página.")

internal fun facebookRetryDelay(error: Throwable, attempt: Int): Long? {
    if (attempt !in 1..5 || error is InterruptedException) return null
    if (error is FacebookRequestFailure && !error.retryable) return null
    if (error !is IOException) return null
    val backoff = (2_000L shl (attempt - 1)).coerceAtMost(30_000)
    return maxOf(backoff, (error as? FacebookRequestFailure)?.retryAfterMillis ?: 0)
}
