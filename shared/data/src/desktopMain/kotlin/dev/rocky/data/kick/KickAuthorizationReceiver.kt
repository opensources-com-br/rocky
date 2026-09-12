package dev.rocky.data.kick

import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.live.ChatMessage
import java.net.URI

internal data class KickAuthorizationReceiver(
    val receiver: KickLocalReceiver,
    val authorizationUri: URI,
)

internal fun createKickAuthorizationReceiver(
    api: KickApi,
    configuration: KickConfiguration,
    onCode: (verifier: String, code: String) -> Unit,
    onMessage: (ChatMessage) -> Unit,
): KickAuthorizationReceiver {
    val authorization = createKickAuthorization(configuration.clientId, configuration.redirectUri)
    val receiver = KickLocalReceiver(
        redirectUri = configuration.redirectUri,
        expectedState = authorization.state,
        publicKeyPem = api.publicKey(),
        onCode = { code -> onCode(authorization.verifier, code) },
        onMessage = onMessage,
    )
    return KickAuthorizationReceiver(receiver, authorization.uri)
}
