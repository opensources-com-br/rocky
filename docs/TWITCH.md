# Twitch connection

Rocky connects to the authenticated user's Twitch chat through the official Device Code Flow and EventSub WebSocket API.

The official build can include Rocky's public client ID with `-ProckyTwitchClientId=...`. When configured, users only need to authorize their account; contributor builds still accept a client ID in settings.

## Setup

1. Register a Twitch application in the [Developer Console](https://dev.twitch.tv/console/apps).
2. Configure it as a public client and copy its Client ID. Rocky never needs the Client Secret.
3. Run Rocky and open **Settings → Platforms**.
4. Paste the Client ID and select **Connect Twitch**.
5. Select **Open Twitch**, sign in on Twitch, and confirm the code shown by Rocky.
6. Close settings after the status changes to **Connected**. New messages from the authenticated channel will appear in the conversation.

The connector requests only `user:read:chat`. The Client ID is saved in the operating system's local preferences. Access and refresh tokens remain in memory and are discarded when the user disconnects or closes Rocky.

## Connection behavior

- EventSub keepalives are monitored while the session is active.
- Twitch-requested reconnect URLs are followed without recreating subscriptions.
- Unexpected disconnects retry after 1, 2, 4, 8, 16, and at most 30 seconds.
- A new subscription is created after an unexpected reconnect, and repeated message IDs are ignored.
- Rocky validates the OAuth token before opening the chat and refreshes an expired token when subscription authorization fails.

This first connector listens to the authenticated user's own channel and reads its current viewer count. It does not load earlier messages, channel points, subscriptions, or paid support events yet.

References: [Twitch OAuth](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/), [token validation](https://dev.twitch.tv/docs/authentication/validate-tokens/), and [EventSub WebSockets](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/).
