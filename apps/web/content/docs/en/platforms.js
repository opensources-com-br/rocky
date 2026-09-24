const content = {
  group: "Configuration",
  title: "Platforms",
  lead: "Twitch, Kick, YouTube, Facebook, and TikTok LIVE provide new messages and the current viewer count for the connected channel.",
  blocks: [
    { type: "h2", text: "Connect Twitch" },
    { type: "list", items: [
      "Open Settings → Platforms and choose Connect Twitch.",
      "If no Client ID is configured, register a public app in the Twitch developer console and copy its Client ID. Rocky does not use a Client Secret.",
      "Open Twitch from the app button and authorize the displayed code. Wait for the Connected state."
    ] },
    { type: "h2", text: "Connect Kick" },
    { type: "list", items: [
      "Create an app at dev.kick.com and register http://localhost:18181/oauth/kick/callback as the callback.",
      "Also register a public HTTPS URL that forwards the webhook body and headers to http://localhost:18181/webhooks/kick.",
      "Enter the Client ID and Client Secret, choose Connect Kick, open authorization, and keep forwarding active during the stream."
    ] },
    { type: "h2", text: "Connect YouTube" },
    { type: "list", items: [
      "Enable YouTube Data API v3 and create Desktop app OAuth credentials.",
      "Enter the Client ID and Client Secret, connect, and authorize the account in your browser.",
      "Keep an active stream with chat enabled so Rocky can find the broadcast."
    ] },
    { type: "h2", text: "Connect Facebook" },
    { type: "list", items: [
      "Create a Business app in Meta for Developers, add Facebook Login, and register http://127.0.0.1:18183/oauth/facebook/callback as the callback.",
      "Request pages_show_list, pages_read_engagement, and pages_read_user_content. Enter the App ID and App Secret in Rocky and authorize the account that manages the Page.",
      "Start a Page live video before connecting. Rocky selects the first authorized Page with an active broadcast and follows new comments."
    ] },
    { type: "h2", text: "Connect TikTok LIVE" },
    { type: "list", items: [
      "Start a public TikTok live and copy the channel username, with or without @.",
      "In Rocky, open Settings → Platforms → TikTok LIVE, enter the username, and choose Connect TikTok.",
      "The connection does not request a password or cookies. It uses the unofficial WebCast protocol through TikTokLiveJava and the Eulerstream service; TikTok changes may interrupt the feature."
    ] },
    { type: "h2", text: "Simultaneous connections" },
    { type: "para", text: "You can connect more than one platform and disconnect each separately. Conversation combines received messages; notes, questions, and the summary stay in one Rocky session until the last platform disconnects." },
    { type: "h2", text: "Data and permissions" },
    { type: "para", text: "Twitch uses Device Code Flow and EventSub WebSocket. Kick uses OAuth 2.1 and signed webhooks. YouTube and Facebook use OAuth and periodically poll chat. TikTok uses the public channel username and unofficial WebCast through Eulerstream. Client Secrets and App Secrets use the system vault; tokens remain in memory only." },
    { type: "h2", text: "What is not integrated" },
    { type: "para", text: "The chips show status and open the disconnect action. Rocky does not send chat messages and does not receive subs, bits, raids, channel points, gifts, Stars, or Super Chats as support events." },
    { type: "h2", text: "Reconnection" },
    { type: "para", text: "Twitch and TikTok try to reconnect automatically. For Kick, keep the HTTPS webhook forwarding to the open app. YouTube and Facebook keep polling while the live video is available. No connector loads the complete history; messages can be lost during downtime." }
  ]
};

export default content;
