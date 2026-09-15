# Connect YouTube

## Recovery and polling

The connector retains paginated HTTP polling, requesting only needed fields and up to 500 messages per response. YouTube's polling interval is respected in full, including values above 30 seconds. Audience refreshes run every 30 seconds; failed refreshes keep the last successful value.

Transient chat-polling failures allow up to 5 retries, delayed by 2, 4, 8, 16 and 30 seconds, never less than the API polling interval. Numeric `Retry-After` values are respected up to 5 minutes. Success resets the budget while preserving the session's cursor and message deduplication.

Exhausted quota, missing permissions, disabled chat or an ended stream stop polling. Disconnect cancels scheduled tasks and interrupts ongoing requests; old events cannot reach a replacement session.

Rocky reads new live-chat messages and the current viewer count from an active broadcast on the authorized channel. It does not send messages, and Super Chats or Super Stickers are not yet added to the Support tab.

## Prepare credentials

1. Open the [Google Cloud Console](https://console.cloud.google.com/apis/credentials) and select or create a project.
2. Enable the **YouTube Data API v3**.
3. Configure the OAuth consent screen. If the app is in testing, add the channel account as a test user.
4. Create an OAuth client of type **Desktop app**.
5. Copy the Client ID and Client Secret shown by Google.

## Connect a stream

1. Start a public or unlisted broadcast with live chat enabled.
2. In Rocky, open **Settings → Platforms**.
3. Enter the Client ID and Client Secret in the YouTube card and choose **Connect YouTube**.
4. Authorize the account in the browser. Google returns to the local callback shown by Rocky.
5. Wait for **Connected**, then send a message from another account to validate chat delivery.

Rocky requests read-only YouTube access. Authorization and refresh tokens remain in session memory; the Client Secret is stored in the system credential vault. The connector discovers the active broadcast, respects the polling interval returned by the API, and does not retrieve the complete stream history.

If no stream is found, confirm that the broadcast belongs to the authorized account, is active, and has live chat enabled. If authorization does not return to Rocky, check that port `18182` is available and try again.
