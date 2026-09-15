package dev.rocky.data.twitch

import java.net.http.WebSocket
import java.util.concurrent.CompletableFuture

internal typealias TwitchAuthenticator = (String, () -> Boolean, (DeviceAuthorization) -> Unit) -> TwitchAuthentication?
internal typealias TwitchSocketConnector = (String, WebSocket.Listener) -> CompletableFuture<WebSocket>
