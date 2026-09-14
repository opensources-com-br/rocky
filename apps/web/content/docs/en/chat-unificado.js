const content = {
  group: "Features",
  title: "Chat and questions",
  lead: "The Conversation tab displays chat received from Twitch, Kick, YouTube, Facebook, or TikTok LIVE. The question queue organizes part of that content; the app uses one platform connection per session.",
  blocks: [
    { type: "h2", text: "Grouped questions" },
    { type: "para", text: "Questions detected by text rules are grouped by similarity and saved by stream. Use the queue to inspect sources and mark them pending or answered. Detection may miss questions; it does not guarantee semantic equivalence or automatically identify everything answered out loud." },
    { type: "h2", text: "Context filters" },
    { type: "list", items: [
      "Commands beginning with ! or / are ignored by default.",
      "Nightbot, StreamElements, Streamlabs, and Moobot are on the initial bot list; the list is editable.",
      "Repeats from the same author and bursts above ten messages in thirty seconds may be excluded from context, along with long character repetitions.",
      "Filters affect AI and the question queue. Displayed chat remains intact. They do not import the platform's muted-user list."
    ] },
    { type: "h2", text: "Question shortcuts" },
    { type: "para", text: "Top questions, What did I miss?, and Chat ideas send prepared requests to AI. Results use the same recent sample; they do not reconstruct missing periods or the entire stream." },
    { type: "h2", text: "Pulse" },
    { type: "para", text: "The Pulse tab shows trends calculated from received samples: messages per minute and viewers for the connected Twitch, Kick, YouTube, Facebook, or TikTok LIVE account. Connection drops or missing data limit the reading." }
  ]
};

export default content;
