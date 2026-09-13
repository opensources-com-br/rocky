# Facebook Live

Rocky receives new comments and the audience reported by the Graph API for an active live video on a Page managed by the authorized account.

## Configure the app

1. Open [Meta for Developers](https://developers.facebook.com/apps/) and create a Business app.
2. Add the Facebook Login product.
3. Register exactly `http://127.0.0.1:18183/oauth/facebook/callback` as a valid OAuth redirect URI.
4. Ensure the app can request `pages_show_list`, `pages_read_engagement`, and `pages_read_user_content`. Accounts outside the app roles may require Live mode and Meta review for these permissions.
5. In Settings → Platforms → Facebook, enter the App ID and App Secret.

The App Secret is stored in macOS Keychain or protected with user-scoped Windows DPAPI. User and Page tokens stay in memory and are discarded when Rocky disconnects or closes.

## Connect the live video

1. Start a live video with comments enabled on a Page managed by the account.
2. Click **Connect Facebook**, then **Open Facebook**.
3. Authorize the Pages Rocky may read.
4. Rocky finds the first authorized Page with an active live video and starts polling new comments.

The connector does not recover full history, post to chat, or process Stars and other paid support. If no authorized Page is live, the connection reports the failure and can be retried after the broadcast starts.
