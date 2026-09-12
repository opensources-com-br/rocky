package dev.rocky.data.kick

import dev.rocky.core.kick.KickAccount
import dev.rocky.core.kick.KickChatClient
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionListener
import dev.rocky.core.kick.KickConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class DesktopKickChatClient : KickChatClient {
    private val api = KickApi()
    private val generation = AtomicLong()
    private val ioExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-kick-io").apply { isDaemon = true }
    }
    @Volatile private var active = false
    @Volatile private var listener = KickConnectionListener {}
    @Volatile private var configuration = KickConfiguration()
    @Volatile private var tokens: KickTokens? = null
    @Volatile private var account: KickAccount? = null
    @Volatile private var receiver: KickLocalReceiver? = null
    @Volatile private var authorizationCompleted = false

    override fun connect(configuration: KickConfiguration, listener: KickConnectionListener) {
        require(configuration.clientId.isNotBlank()) { "Informe o Client ID da Kick." }
        require(configuration.clientSecret.isNotBlank()) { "Informe o Client Secret da Kick." }
        val run = generation.incrementAndGet()
        this.configuration = configuration.copy(
            clientId = configuration.clientId.trim(),
            clientSecret = configuration.clientSecret.trim(),
            redirectUri = configuration.redirectUri.trim(),
        )
        this.listener = listener
        active = true
        authorizationCompleted = false
        emit(KickConnectionPhase.Authenticating, "Preparando autorização da Kick")
        ioExecutor.execute {
            runCatching {
                createKickAuthorizationReceiver(
                    api = api,
                    configuration = this.configuration,
                    onCode = { verifier, code -> completeAuthorization(run, verifier, code) },
                    onMessage = { message ->
                        if (isCurrent(run) && message.channelId == account?.userId) {
                            listener.onEvent(KickConnectionEvent.MessageReceived(message))
                        }
                    },
                ).also { receiver = it.receiver }
            }.onSuccess { authorization ->
                if (isCurrent(run)) {
                    emit(KickConnectionPhase.AwaitingAuthorization, "Autorize a conta no navegador")
                    listener.onEvent(KickConnectionEvent.AuthorizationRequired(authorization.authorizationUri))
                }
            }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    @Synchronized
    private fun completeAuthorization(run: Long, verifier: String, code: String) {
        if (!isCurrent(run) || authorizationCompleted) return
        authorizationCompleted = true
        emit(KickConnectionPhase.Connecting, "Conectando ao chat da Kick")
        ioExecutor.execute {
            runCatching {
                val current = configuration
                val newTokens = api.exchangeCode(current.clientId, current.clientSecret,
                    current.redirectUri, verifier, code)
                newTokens to api.account(newTokens.accessToken)
            }.onSuccess { (newTokens, newAccount) ->
                if (isCurrent(run)) {
                    tokens = newTokens
                    account = newAccount
                    listener.onEvent(KickConnectionEvent.Connected(newAccount))
                }
            }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    override fun disconnect() {
        active = false
        generation.incrementAndGet()
        emit(KickConnectionPhase.Disconnected)
    }

    override fun close() {
        active = false
        ioExecutor.shutdownNow()
    }

    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        active = false
        receiver?.close()
        receiver = null
        emit(KickConnectionPhase.Failed, message)
    }

    private fun emit(phase: KickConnectionPhase, detail: String? = null) =
        listener.onEvent(KickConnectionEvent.PhaseChanged(phase, detail))

    private fun isCurrent(run: Long) = active && generation.get() == run
    private fun Throwable.userMessage() = message?.takeIf { it.isNotBlank() }
        ?: "Não foi possível conectar com a Kick."
}
