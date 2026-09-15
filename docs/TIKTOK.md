# Connect TikTok LIVE

TikTok does not expose LIVE chat or audience data through its public developer API. The Rocky desktop app therefore connects to a live stream's public WebCast through the community TikTokLiveJava library and the Eulerstream signing service.

## Connect

1. Start a public TikTok LIVE.
2. In Rocky, open **Settings > Platforms > TikTok LIVE**.
3. Enter the `@username` of the account that is streaming.
4. Select **Connect TikTok**.

Rocky does not request a TikTok password, cookie, token, or Client Secret. Only the username is saved in local preferences. The connection sends the username and public room identifier to TikTok and Eulerstream as required to open the live WebSocket.

## Connection recovery

Rocky manages connection attempts in the background. Each attempt has a 20-second connection deadline. Temporary failures or chat disconnects trigger up to five consecutive retries after 2, 4, 8, 16, and 30 seconds. A successful connection resets that budget. The library does not run a competing retry loop.

The status shows the retry number and delay. Duplicate comments are filtered across reconnections to the same room; callbacks from previous attempts are ignored. Messages sent during a disconnect are not guaranteed to be recovered.

An identified offline/unknown account or LIVE-ended event stops recovery without retries. Unclassified failures use the bounded retry policy, then show a failure when attempts are exhausted. Check the live and select **Connect TikTok** again. **Disconnect** or closing Rocky cancels retries.

User-facing errors do not expose raw service responses, private URLs, or credentials. Recovery was tested with simulated transports; actual latency and availability still depend on TikTok and Eulerstream.

## Received events

The connector receives new comments and viewer updates from the current live stream. Gifts, likes, follows, subscriptions, paid events, and sending chat messages are not integrated into Rocky.

This integration relies on an unofficial protocol and may stop working when TikTok changes WebCast. Rocky reports a connection failure when the account is offline or private, the live is age/region restricted, or the connection service is unavailable.
