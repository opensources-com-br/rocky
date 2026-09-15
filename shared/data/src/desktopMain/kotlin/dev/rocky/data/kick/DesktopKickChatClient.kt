package dev.rocky.data.kick

import dev.rocky.core.kick.KickAccount
import dev.rocky.core.kick.KickChatClient
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionListener
import dev.rocky.core.kick.KickConnectionPhase
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DesktopKickChatClient internal constructor(
    private val api: KickApi,
    private val subscriptions: KickEventSubscriptions,
) : KickChatClient {
    constructor() : this(KickApi(), KickEventSubscriptions())
    private val generation = AtomicLong()
    private val cleanupExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-kick-cleanup").apply { isDaemon = true }
    }
    private val ioExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-kick-io").apply { isDaemon = true }
    }
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { task ->
        Thread(task, "rocky-kick-metrics").apply { isDaemon = true }
    }
    @Volatile private var active = false
    @Volatile private var listener = KickConnectionListener {}
    @Volatile private var configuration = KickConfiguration()
    @Volatile private var tokens: KickTokens? = null
    @Volatile private var account: KickAccount? = null
    @Volatile private var receiver: KickLocalReceiver? = null
    @Volatile private var subscriptionIds = emptyList<String>()
    @Volatile private var authorizationCompleted = false
    private var pendingRequest: Future<*>? = null
    private var pendingAudience: ScheduledFuture<*>? = null
    private var audienceRetryAttempt = 0
    @Volatile private var closed = false

    @Synchronized
    private fun scheduleAudience(run: Long, delayMillis: Long) {
        if (!isCurrent(run) || scheduler.isShutdown) return
        pendingAudience?.cancel(false)
        pendingAudience = scheduler.schedule({ refreshAudience(run) }, delayMillis, TimeUnit.MILLISECONDS)
    }

    @Synchronized
    override fun connect(configuration: KickConfiguration, listener: KickConnectionListener) {
        check(!closed) { "O cliente da Kick já foi encerrado." }
        stop(notify = false)
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
        val currentConfiguration = this.configuration
        pendingRequest = ioExecutor.submit {
            if (!isCurrent(run)) return@submit
            runCatching {
                createKickAuthorizationReceiver(
                    api = api,
                    configuration = currentConfiguration,
                    onCode = { verifier, code -> completeAuthorization(run, verifier, code) },
                    onMessage = { message ->
                        synchronized(this) {
                            if (message.channelId == account?.userId)
                                emitForRun(run, KickConnectionEvent.MessageReceived(message))
                        }
                    },
                ).also { authorization -> synchronized(this) {
                    if (isCurrent(run)) receiver = authorization.receiver
                    else authorization.receiver.close()
                } }
            }.onSuccess { authorization -> synchronized(this) {
                if (isCurrent(run)) {
                    emit(KickConnectionPhase.AwaitingAuthorization, "Autorize a conta no navegador")
                    emitForRun(run, KickConnectionEvent.AuthorizationRequired(authorization.authorizationUri))
                }
            } }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    @Synchronized
    private fun completeAuthorization(run: Long, verifier: String, code: String) {
        if (!isCurrent(run) || authorizationCompleted) return
        authorizationCompleted = true
        emit(KickConnectionPhase.Connecting, "Conectando ao chat da Kick")
        val current = configuration
        pendingRequest = ioExecutor.submit {
            if (!isCurrent(run)) return@submit
            runCatching {
                val newTokens = api.exchangeCode(current.clientId, current.clientSecret,
                    current.redirectUri, verifier, code)
                val newAccount = api.account(newTokens.accessToken)
                val newSubscriptionIds = subscriptions.subscribeToChat(newTokens.accessToken)
                Triple(newTokens, newAccount, newSubscriptionIds)
            }.onSuccess { (newTokens, newAccount, newSubscriptionIds) -> synchronized(this) {
                if (isCurrent(run)) {
                    tokens = newTokens
                    account = newAccount
                    subscriptionIds = newSubscriptionIds
                    listener.onEvent(KickConnectionEvent.Connected(newAccount))
                    refreshAudience()
                } else {
                    cleanupExecutor.execute { runCatching {
                        subscriptions.unsubscribe(newTokens.accessToken, newSubscriptionIds)
                    } }
                }
            } }.onFailure { error -> if (isCurrent(run)) fail(run, error.userMessage()) }
        }
    }

    @Synchronized
    private fun refreshAudience(run: Long = generation.get()) {
        val currentTokens = tokens ?: return
        if (!isCurrent(run) || account == null) return
        val current = configuration
        pendingRequest = ioExecutor.submit {
            if (!isCurrent(run)) return@submit
            runCatching { api.viewerCount(currentTokens.accessToken) }
                .recoverCatching { error ->
                    if (error !is KickApiException || error.statusCode != 401) throw error
                    val refreshed = api.refresh(current.clientId, current.clientSecret, currentTokens.refreshToken)
                    synchronized(this) { if (isCurrent(run)) tokens = refreshed }
                    api.viewerCount(refreshed.accessToken)
                }
                .onSuccess { count -> synchronized(this) {
                    if (isCurrent(run)) {
                        audienceRetryAttempt = 0
                        emitForRun(run, KickConnectionEvent.AudienceUpdated(count))
                        scheduleAudience(run, 30_000)
                    }
                } }
                .onFailure { handleAudienceFailure(run, it) }
        }
    }

    override fun disconnect() = stop(notify = true)

    @Synchronized
    override fun close() {
        if (closed) return
        closed = true
        stop(notify = false)
        scheduler.shutdownNow()
        ioExecutor.shutdownNow()
        cleanupExecutor.shutdown()
    }

    @Synchronized
    private fun stop(notify: Boolean) {
        val oldTokens = tokens
        val oldSubscriptions = subscriptionIds
        active = false
        generation.incrementAndGet()
        pendingAudience?.cancel(false)
        pendingAudience = null
        pendingRequest?.cancel(true)
        pendingRequest = null
        audienceRetryAttempt = 0
        receiver?.close()
        receiver = null
        tokens = null
        account = null
        subscriptionIds = emptyList()
        authorizationCompleted = false
        if (oldTokens != null && oldSubscriptions.isNotEmpty() && !cleanupExecutor.isShutdown) {
            cleanupExecutor.execute { runCatching { subscriptions.unsubscribe(oldTokens.accessToken, oldSubscriptions) } }
        }
        if (notify) emit(KickConnectionPhase.Disconnected)
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

    @Synchronized
    private fun emitForRun(run: Long, event: KickConnectionEvent) {
        if (isCurrent(run)) listener.onEvent(event)
    }

    private fun isCurrent(run: Long) = !closed && active && generation.get() == run
    private fun Throwable.userMessage() = message?.takeIf { it.isNotBlank() }
        ?: "Não foi possível conectar com a Kick."
}
