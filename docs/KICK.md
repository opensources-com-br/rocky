# Kick connection

Rocky connects to the authenticated channel with Kick's official OAuth 2.1 and Events APIs. It reads new `chat.message.sent` events and the current viewer count. It does not read chat history or send messages.

## Requirements

- a Kick developer application with a Client ID and Client Secret;
- the OAuth callback `http://localhost:18181/oauth/kick/callback`;
- a public HTTPS webhook URL that forwards requests unchanged to `http://localhost:18181/webhooks/kick` while Rocky is running.

Kick delivers chat events only through webhooks. A local desktop address is not reachable from Kick, so chat capture requires an HTTPS endpoint or tunnel configured in the Kick developer application. Forward the raw request body and every `Kick-Event-*` header without modification.

## Connect

1. Open **Settings → Platforms → Kick → Create app**.
2. Register the callback and webhook described above in Kick Dev.
3. Copy the Client ID and Client Secret into Rocky. Change the local callback only if the same value is registered in Kick Dev.
4. Select **Connect Kick**, then **Open Kick**, and authorize the requested permissions.
5. Keep the HTTPS forwarding endpoint active during the live stream. Rocky changes to **Connected** after it creates the chat subscription.

Rocky requests `user:read`, `channel:read`, and `events:subscribe`. The Client Secret is stored in macOS Keychain or protected with user-scoped Windows DPAPI. Access and refresh tokens remain in memory and are discarded when the app closes.

## Event handling

Rocky verifies every webhook with Kick's published RSA key, timestamp, message ID, and raw body. Repeated event IDs are acknowledged and ignored. Disconnecting removes the active event subscription when Kick is reachable.

If the OAuth callback works but no messages arrive, check the public webhook configuration and HTTPS forwarding first. Rocky cannot recover messages sent while the webhook or app was unavailable.

References: [Kick OAuth](https://github.com/KickEngineering/KickDevDocs/blob/main/getting-started/generating-tokens-oauth2-flow.md), [event subscriptions](https://github.com/KickEngineering/KickDevDocs/blob/main/events/subscribe-to-events.md), and [webhook security](https://github.com/KickEngineering/KickDevDocs/blob/main/events/webhook-security.md).
