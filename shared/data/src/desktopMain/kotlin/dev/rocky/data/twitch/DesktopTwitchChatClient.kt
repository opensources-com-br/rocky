package dev.rocky.data.twitch

import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionListener
import dev.rocky.core.twitch.TwitchConnectionPhase
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.io.IOException
import java.time.Duration
import java.util.LinkedHashSet
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DesktopTwitchChatClient : TwitchChatClient {
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build()
    private val api = TwitchApi(httpClient)
    private val deviceFlow = TwitchDeviceFlow(api)
    private val generation = AtomicLong()
    private val ioExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "rocky-twitch-io").apply { isDaemon = true }
    }
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { task ->
        Thread(task, "rocky-twitch-reconnect").apply { isDaemon = true }
    }
    private val seenMessageIds = LinkedHashSet<String>()

    @Volatile
    private var active = false

    @Volatile
    private var listener = TwitchConnectionListener {}

    @Volatile
    private var clientId = ""

    @Volatile
    private var tokens: TwitchTokens? = null

    @Volatile
    private var account: TwitchAccount? = null

    @Volatile
    private var socket: WebSocket? = null

    @Volatile
    private var reconnectScheduled = false

    @Volatile
    private var lastEventAt = 0L

    @Volatile
    private var keepaliveTimeoutMillis = 0L

    @Volatile
    private var lastValidationAt = 0L

    @Volatile
    private var validationRunning = false

    private var reconnectAttempt = 0
    private var validationRetryAttempt = 0

    init {
        scheduler.scheduleAtFixedRate(::checkKeepalive, 5, 5, TimeUnit.SECONDS)
    }

    override fun connect(clientId: String, listener: TwitchConnectionListener) {
        stopConnection(notify = false)
        val run = generation.incrementAndGet()
        this.clientId = clientId.trim()
        this.listener = listener
        active = true
        reconnectAttempt = 0
        validationRetryAttempt = 0
        seenMessageIds.clear()
        emit(TwitchConnectionPhase.Authenticating, "Solicitando autorização da Twitch")

        ioExecutor.execute {
            runCatching {
                deviceFlow.authenticate(
                    clientId = this.clientId,
                    isActive = { isCurrent(run) },
                    onAuthorization = { authorization ->
                        if (isCurrent(run)) {
                            listener.onEvent(
                                TwitchConnectionEvent.AuthorizationRequired(
                                    userCode = authorization.userCode,
                                    verificationUri = authorization.verificationUri,
                                ),
                            )
                        }
                    },
                )
            }.onSuccess { authentication ->
                if (authentication == null) {
                    if (isCurrent(run)) fail(run, "A autorização expirou. Tente novamente.")
                } else if (isCurrent(run)) {
                    tokens = authentication.tokens
                    account = authentication.account
                    lastValidationAt = System.currentTimeMillis()
                    emit(TwitchConnectionPhase.Connecting, "Conectando ao chat")
                    openSocket(DEFAULT_WEBSOCKET_URL, run)
                }
            }.onFailure { error ->
                if (error !is InterruptedException && isCurrent(run)) {
                    fail(run, error.userMessage("Não foi possível autenticar com a Twitch."))
                }
            }
        }
    }

    override fun disconnect() {
        stopConnection(notify = true)
    }

    override fun close() {
        stopConnection(notify = false)
        ioExecutor.shutdownNow()
        scheduler.shutdownNow()
    }

    private fun openSocket(
        url: String,
        run: Long,
        transferFrom: WebSocket? = null,
    ) {
        if (!isCurrent(run)) return
        val webSocketListener = TwitchWebSocketListener(
            onOpened = { opened ->
                if (isCurrent(run)) {
                    socket = opened
                } else {
                    opened.sendClose(WebSocket.NORMAL_CLOSURE, "stale connection")
                }
            },
            onEvent = { opened, event -> handleSocketEvent(opened, event, run, transferFrom) },
            onClosed = { closed, _ ->
                if (isCurrent(run) && socket === closed) scheduleReconnect(run)
            },
        )
        httpClient.newWebSocketBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .buildAsync(URI.create(url), webSocketListener)
            .whenComplete { _, error ->
                if (error != null && isCurrent(run)) scheduleReconnect(run)
            }
    }

    private fun handleSocketEvent(
        webSocket: WebSocket,
        event: TwitchSocketEvent,
        run: Long,
        transferFrom: WebSocket?,
    ) {
        if (!isCurrent(run)) return
        lastEventAt = System.currentTimeMillis()
        when (event) {
            is TwitchSocketEvent.Welcome -> {
                keepaliveTimeoutMillis = event.keepaliveTimeoutSeconds * 1_000
                if (transferFrom != null) {
                    transferFrom.sendClose(WebSocket.NORMAL_CLOSURE, "reconnected")
                    markConnected(run)
                } else {
                    subscribeToChat(webSocket, event.sessionId, run)
                }
            }
            TwitchSocketEvent.Keepalive,
            TwitchSocketEvent.Unknown -> Unit
            is TwitchSocketEvent.Reconnect -> {
                emit(TwitchConnectionPhase.Reconnecting, "A Twitch solicitou uma nova conexão")
                openSocket(event.url, run, transferFrom = webSocket)
            }
            is TwitchSocketEvent.MessageReceived -> {
                if (rememberMessage(event.message.id)) {
                    listener.onEvent(TwitchConnectionEvent.MessageReceived(event.message))
                }
            }
            is TwitchSocketEvent.Revoked -> fail(run, "A Twitch revogou o acesso: ${event.reason}")
        }
    }

    private fun subscribeToChat(webSocket: WebSocket, sessionId: String, run: Long) {
        ioExecutor.execute {
            if (!isCurrent(run) || socket !== webSocket) return@execute
            runCatching {
                val currentAccount = requireNotNull(account)
                var currentTokens = requireNotNull(tokens)
                try {
                    api.subscribeToChat(clientId, currentTokens.accessToken, currentAccount, sessionId)
                } catch (error: TwitchApiException) {
                    if (error.statusCode != 401) throw error
                    currentTokens = api.refreshTokens(clientId, currentTokens.refreshToken)
                    if (!isCurrent(run)) return@execute
                    tokens = currentTokens
                    api.subscribeToChat(clientId, currentTokens.accessToken, currentAccount, sessionId)
                }
            }.onSuccess {
                if (isCurrent(run) && socket === webSocket) markConnected(run)
            }.onFailure { error ->
                if (isCurrent(run) && socket === webSocket) {
                    if (error.isTransientTwitchFailure()) {
                        webSocket.abort()
                        scheduleReconnect(run)
                    } else {
                        fail(run, error.userMessage("Não foi possível assinar o chat da Twitch."))
                    }
                }
            }
        }
    }

    private fun markConnected(run: Long) {
        if (!isCurrent(run)) return
        reconnectAttempt = 0
        reconnectScheduled = false
        val currentAccount = account ?: return
        listener.onEvent(TwitchConnectionEvent.Connected(currentAccount))
    }

    @Synchronized
    private fun scheduleReconnect(run: Long) {
        if (!isCurrent(run) || reconnectScheduled) return
        reconnectScheduled = true
        reconnectAttempt += 1
        val delaySeconds = twitchReconnectDelaySeconds(reconnectAttempt)
        emit(TwitchConnectionPhase.Reconnecting, "Reconectando em $delaySeconds s")
        scheduler.schedule(
            {
                if (isCurrent(run)) {
                    reconnectScheduled = false
                    openSocket(DEFAULT_WEBSOCKET_URL, run)
                }
            },
            delaySeconds,
            TimeUnit.SECONDS,
        )
    }

    @Synchronized
    private fun rememberMessage(messageId: String): Boolean {
        if (!seenMessageIds.add(messageId)) return false
        if (seenMessageIds.size > MAX_SEEN_MESSAGES) {
            seenMessageIds.remove(seenMessageIds.first())
        }
        return true
    }

    private fun checkKeepalive() {
        checkTokenValidation()
        val timeout = keepaliveTimeoutMillis
        if (!active || timeout == 0L || System.currentTimeMillis() - lastEventAt <= timeout + 5_000) return
        val currentRun = generation.get()
        keepaliveTimeoutMillis = 0
        socket?.abort()
        scheduleReconnect(currentRun)
    }

    @Synchronized
    private fun checkTokenValidation() {
        val now = System.currentTimeMillis()
        val run = generation.get()
        if (!isCurrent(run) || tokens == null || validationRunning || now - lastValidationAt < TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS) return
        validationRunning = true
        val validationClientId = clientId
        val validationTokens = tokens ?: return
        ioExecutor.execute {
            if (!isCurrent(run)) return@execute
            runCatching {
                validateTwitchTokens(
                    validationTokens,
                    validate = { api.validate(it) },
                    refresh = { api.refreshTokens(validationClientId, it) },
                )
            }.onSuccess { refreshed ->
                if (isCurrent(run)) {
                    tokens = refreshed
                    lastValidationAt = System.currentTimeMillis()
                    validationRetryAttempt = 0
                    validationRunning = false
                }
            }.onFailure { error ->
                if (isCurrent(run)) {
                    validationRunning = false
                    if (error.requiresNewTwitchAuthorization()) {
                        fail(run, "A sessão da Twitch deixou de ser válida. Conecte novamente.")
                    } else {
                        validationRetryAttempt += 1
                        lastValidationAt = nextValidationRetryReferenceTime(
                            now = System.currentTimeMillis(),
                            attempt = validationRetryAttempt,
                        )
                    }
                }
            }
        }
    }

    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        active = false
        socket?.sendClose(WebSocket.NORMAL_CLOSURE, "failed")
        socket = null
        emit(TwitchConnectionPhase.Failed, message)
    }

    private fun stopConnection(notify: Boolean) {
        active = false
        generation.incrementAndGet()
        reconnectScheduled = false
        keepaliveTimeoutMillis = 0
        lastValidationAt = 0
        validationRunning = false
        validationRetryAttempt = 0
        socket?.sendClose(WebSocket.NORMAL_CLOSURE, "disconnected")
        socket = null
        tokens = null
        account = null
        if (notify) emit(TwitchConnectionPhase.Disconnected)
    }

    private fun emit(phase: TwitchConnectionPhase, detail: String? = null) {
        listener.onEvent(TwitchConnectionEvent.PhaseChanged(phase, detail))
    }

    private fun isCurrent(run: Long): Boolean = active && generation.get() == run

    private fun Throwable.userMessage(fallback: String): String =
        (this as? TwitchApiException)?.twitchMessage ?: fallback

    private companion object {
        const val DEFAULT_WEBSOCKET_URL = "wss://eventsub.wss.twitch.tv/ws?keepalive_timeout_seconds=30"
        const val MAX_SEEN_MESSAGES = 1_000
    }
}

internal const val TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS = 60 * 60 * 1_000L

internal fun Throwable.isTransientTwitchFailure(): Boolean =
    this is IOException || (this is TwitchApiException && (statusCode == 429 || statusCode in 500..599))

internal fun Throwable.requiresNewTwitchAuthorization(): Boolean =
    this is TwitchApiException && statusCode in setOf(400, 401, 403)

internal fun nextValidationRetryReferenceTime(now: Long, attempt: Int): Long =
    now - TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS +
        twitchReconnectDelaySeconds(attempt) * 1_000
