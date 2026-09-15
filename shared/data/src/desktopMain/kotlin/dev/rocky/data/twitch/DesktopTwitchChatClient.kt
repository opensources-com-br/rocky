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
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DesktopTwitchChatClient internal constructor(
    private val httpClient: HttpClient,
    private val api: TwitchApi = TwitchApi(httpClient),
    private val authenticate: TwitchAuthenticator = TwitchDeviceFlow(api)::authenticate,
    private val socketConnector: TwitchSocketConnector = { url, listener ->
        httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(20))
            .buildAsync(URI.create(url), listener)
    },
    private val welcomeTimeoutSeconds: Long = 20,
) : TwitchChatClient {
    constructor() : this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build())
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

    @Volatile
    private var audienceRefreshRunning = false

    @Volatile
    private var lastAudienceRefreshAt = 0L

    private var reconnectAttempt = 0
    private var validationRetryAttempt = 0
    private var closed = false
    private val ioTasks = mutableListOf<Future<*>>()
    private var reconnectTask: ScheduledFuture<*>? = null
    private var welcomeTask: ScheduledFuture<*>? = null
    private var pendingSocket: WebSocket? = null
    private var socketAttempt = 0L
    private var socketOpening: Future<*>? = null

    init {
        scheduler.scheduleAtFixedRate(::checkKeepalive, 5, 5, TimeUnit.SECONDS)
    }

    @Synchronized
    override fun connect(clientId: String, listener: TwitchConnectionListener) {
        check(!closed) { "O cliente da Twitch já foi encerrado." }
        require(clientId.isNotBlank()) { "Informe o Client ID da Twitch." }
        stopConnection(notify = false)
        val run = generation.incrementAndGet()
        this.clientId = clientId.trim()
        this.listener = listener
        active = true
        reconnectAttempt = 0
        validationRetryAttempt = 0
        seenMessageIds.clear()
        emit(TwitchConnectionPhase.Authenticating, "Solicitando autorização da Twitch")

        val authenticationClientId = this.clientId
        submitIo(run) {
            runCatching {
                authenticate(
                    authenticationClientId,
                    { isCurrent(run) },
                    { authorization ->
                        if (isCurrent(run)) {
                            emitForRun(run,
                                TwitchConnectionEvent.AuthorizationRequired(
                                    userCode = authorization.userCode,
                                    verificationUri = authorization.verificationUri,
                                ),
                            )
                        }
                    },
                )
            }.onSuccess { authentication -> synchronized(this) {
                if (authentication == null) {
                    if (isCurrent(run)) fail(run, "A autorização expirou. Tente novamente.")
                } else if (isCurrent(run)) {
                    tokens = authentication.tokens
                    account = authentication.account
                    lastValidationAt = System.currentTimeMillis()
                    emit(TwitchConnectionPhase.Connecting, "Conectando ao chat")
                    openSocket(DEFAULT_WEBSOCKET_URL, run)
                }
            } }.onFailure { error ->
                if (error !is InterruptedException && isCurrent(run)) {
                    fail(run, error.twitchUserMessage("Não foi possível autenticar com a Twitch."))
                }
            }
        }
    }

    override fun disconnect() {
        stopConnection(notify = true)
    }

    @Synchronized
    private fun submitIo(run: Long, task: () -> Unit) {
        if (!isCurrent(run) || ioExecutor.isShutdown) return
        ioTasks.removeAll { it.isDone }
        ioTasks += ioExecutor.submit { if (isCurrent(run)) task() }
    }

    @Synchronized
    override fun close() {
        if (closed) return
        closed = true
        stopConnection(notify = false)
        ioExecutor.shutdownNow()
        scheduler.shutdownNow()
    }

    @Synchronized
    private fun openSocket(
        url: String,
        run: Long,
        transferFrom: WebSocket? = null,
    ) {
        if (!isCurrent(run)) return
        val attempt = ++socketAttempt
        val webSocketListener = TwitchWebSocketListener(
            onOpened = { opened -> synchronized(this) {
                if (isCurrent(run) && attempt == socketAttempt) {
                    pendingSocket = opened
                    welcomeTask?.cancel(false)
                    welcomeTask = scheduler.schedule({ synchronized(this) {
                        if (isCurrent(run) && pendingSocket === opened) scheduleReconnect(run)
                    } }, welcomeTimeoutSeconds, TimeUnit.SECONDS)
                } else {
                    opened.abort()
                }
            } },
            onEvent = { opened, event -> handleSocketEvent(opened, event, run, transferFrom) },
            onClosed = { closed, _ -> synchronized(this) {
                if (isCurrent(run) && (socket === closed || pendingSocket === closed)) scheduleReconnect(run)
            } },
        )
        socketOpening = socketConnector(url, webSocketListener)
            .whenComplete { _, error -> synchronized(this) {
                if (error != null && isCurrent(run) && attempt == socketAttempt) scheduleReconnect(run)
            } }
    }

    @Synchronized
    private fun handleSocketEvent(
        webSocket: WebSocket,
        event: TwitchSocketEvent,
        run: Long,
        transferFrom: WebSocket?,
    ) {
        if (!isCurrent(run) || (socket !== webSocket && pendingSocket !== webSocket)) return
        lastEventAt = System.currentTimeMillis()
        when (event) {
            is TwitchSocketEvent.Welcome -> {
                if (pendingSocket !== webSocket) return
                socket = webSocket
                pendingSocket = null
                welcomeTask?.cancel(false)
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
                if (pendingSocket != null || socket !== webSocket) return
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
        submitIo(run) {
            if (!isCurrent(run) || socket !== webSocket) return@submitIo
            runCatching {
                val currentAccount = requireNotNull(account)
                var currentTokens = requireNotNull(tokens)
                try {
                    api.subscribeToChat(clientId, currentTokens.accessToken, currentAccount, sessionId)
                } catch (error: TwitchApiException) {
                    if (error.statusCode != 401) throw error
                    currentTokens = api.refreshTokens(clientId, currentTokens.refreshToken)
                    if (!isCurrent(run)) return@submitIo
                    synchronized(this) { if (isCurrent(run)) tokens = currentTokens }
                    api.subscribeToChat(clientId, currentTokens.accessToken, currentAccount, sessionId)
                }
            }.onSuccess { synchronized(this) {
                if (isCurrent(run) && socket === webSocket) markConnected(run)
            } }.onFailure { error ->
                if (isCurrent(run) && socket === webSocket) {
                    if (error.isTransientTwitchFailure()) {
                        webSocket.abort()
                        scheduleReconnect(run)
                    } else {
                        fail(run, error.twitchUserMessage("Não foi possível assinar o chat da Twitch."))
                    }
                }
            }
        }
    }

    @Synchronized
    private fun markConnected(run: Long) {
        if (!isCurrent(run)) return
        reconnectAttempt = 0
        reconnectScheduled = false
        val currentAccount = account ?: return
        listener.onEvent(TwitchConnectionEvent.Connected(currentAccount))
    }

    private fun releaseSockets() {
        socketAttempt++
        welcomeTask?.cancel(false)
        socketOpening?.cancel(true)
        socketOpening = null
        pendingSocket?.abort()
        pendingSocket = null
        socket?.abort()
        socket = null
        keepaliveTimeoutMillis = 0
    }

    @Synchronized
    private fun scheduleReconnect(run: Long) {
        if (!isCurrent(run) || reconnectScheduled) return
        reconnectScheduled = true
        releaseSockets()
        reconnectAttempt += 1
        val delaySeconds = twitchReconnectDelaySeconds(reconnectAttempt)
        emit(TwitchConnectionPhase.Reconnecting, "Reconectando em $delaySeconds s")
        reconnectTask = scheduler.schedule(
            { synchronized(this) {
                if (isCurrent(run)) {
                    reconnectScheduled = false
                    openSocket(DEFAULT_WEBSOCKET_URL, run)
                }
            } },
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

    @Synchronized
    private fun checkKeepalive() {
        checkTokenValidation()
        checkAudience()
        val timeout = keepaliveTimeoutMillis
        if (!active || timeout == 0L || System.currentTimeMillis() - lastEventAt <= timeout + 5_000) return
        val currentRun = generation.get()
        keepaliveTimeoutMillis = 0
        socket?.abort()
        scheduleReconnect(currentRun)
    }

    @Synchronized
    private fun checkAudience() {
        val now = System.currentTimeMillis()
        val run = generation.get()
        val currentTokens = tokens ?: return
        val currentAccount = account ?: return
        if (!isCurrent(run) || audienceRefreshRunning || now - lastAudienceRefreshAt < TWITCH_AUDIENCE_REFRESH_INTERVAL_MILLIS) return
        audienceRefreshRunning = true
        lastAudienceRefreshAt = now
        val audienceClientId = clientId
        submitIo(run) {
            runCatching {
                api.viewerCount(audienceClientId, currentTokens.accessToken, currentAccount.userId)
            }.onSuccess { viewerCount ->
                emitForRun(run, TwitchConnectionEvent.AudienceUpdated(viewerCount))
            }
            synchronized(this) { if (isCurrent(run)) audienceRefreshRunning = false }
        }
    }

    @Synchronized
    private fun checkTokenValidation() {
        val now = System.currentTimeMillis()
        val run = generation.get()
        if (!isCurrent(run) || tokens == null || validationRunning || now - lastValidationAt < TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS) return
        validationRunning = true
        val validationClientId = clientId
        val validationTokens = tokens ?: return
        submitIo(run) {
            if (!isCurrent(run)) return@submitIo
            runCatching {
                validateTwitchTokens(
                    validationTokens,
                    validate = { api.validate(it) },
                    refresh = { api.refreshTokens(validationClientId, it) },
                )
            }.onSuccess { refreshed -> synchronized(this) {
                if (isCurrent(run)) {
                    tokens = refreshed
                    lastValidationAt = System.currentTimeMillis()
                    validationRetryAttempt = 0
                    validationRunning = false
                }
            } }.onFailure { error -> synchronized(this) {
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
            } }
        }
    }

    @Synchronized
    private fun fail(run: Long, message: String) {
        if (!isCurrent(run)) return
        stopConnection(notify = false)
        emit(TwitchConnectionPhase.Failed, message)
    }

    @Synchronized
    private fun stopConnection(notify: Boolean) {
        active = false
        generation.incrementAndGet()
        ioTasks.forEach { it.cancel(true) }
        ioTasks.clear()
        reconnectTask?.cancel(false)
        welcomeTask?.cancel(false)
        reconnectScheduled = false
        reconnectTask?.cancel(false)
        keepaliveTimeoutMillis = 0
        lastValidationAt = 0
        validationRunning = false
        audienceRefreshRunning = false
        lastAudienceRefreshAt = 0
        validationRetryAttempt = 0
        releaseSockets()
        tokens = null
        account = null
        if (notify) emit(TwitchConnectionPhase.Disconnected)
    }

    private fun emit(phase: TwitchConnectionPhase, detail: String? = null) {
        listener.onEvent(TwitchConnectionEvent.PhaseChanged(phase, detail))
    }

    @Synchronized
    private fun emitForRun(run: Long, event: TwitchConnectionEvent) {
        if (isCurrent(run)) listener.onEvent(event)
    }

    private fun isCurrent(run: Long): Boolean = !closed && active && generation.get() == run

    private companion object {
        const val DEFAULT_WEBSOCKET_URL = "wss://eventsub.wss.twitch.tv/ws?keepalive_timeout_seconds=30"
        const val MAX_SEEN_MESSAGES = 1_000
    }
}

internal const val TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS = 60 * 60 * 1_000L
internal const val TWITCH_AUDIENCE_REFRESH_INTERVAL_MILLIS = 30_000L

internal fun Throwable.isTransientTwitchFailure(): Boolean =
    this is IOException || (this is TwitchApiException && (statusCode == 429 || statusCode in 500..599))

internal fun Throwable.requiresNewTwitchAuthorization(): Boolean =
    this is TwitchApiException && statusCode in setOf(400, 401, 403)

internal fun nextValidationRetryReferenceTime(now: Long, attempt: Int): Long =
    now - TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS +
        twitchReconnectDelaySeconds(attempt) * 1_000
