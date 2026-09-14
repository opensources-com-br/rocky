# Connect TikTok LIVE

TikTok does not expose LIVE chat or audience data through its public developer API. The Rocky desktop app therefore connects to a live stream's public WebCast through the community TikTokLiveJava library and the Eulerstream signing service.

## Connect

1. Start a public TikTok LIVE.
2. In Rocky, open **Settings > Platforms > TikTok LIVE**.
3. Enter the `@username` of the account that is streaming.
4. Select **Connect TikTok**.

Rocky does not request a TikTok password, cookie, token, or Client Secret. Only the username is saved in local preferences. The connection sends the username and public room identifier to TikTok and Eulerstream as required to open the live WebSocket.

## Current scope

The connector receives new comments and viewer updates from the current live stream. Gifts, likes, follows, subscriptions, paid events, and sending chat messages are not integrated into Rocky.

This integration relies on an unofficial protocol and may stop working when TikTok changes WebCast. Rocky reports a connection failure when the account is offline or private, the live is age/region restricted, or the connection service is unavailable.
