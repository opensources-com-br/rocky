const content = {
  group: "Getting started",
  title: "Concepts",
  lead: "Conversation, the question queue, and records serve different purposes in Rocky.",
  blocks: [
    { type: "h2", text: "During the session" },
    { type: "cards", items: [
      { key: "chat", text: "New Twitch, Kick, YouTube, Facebook, or TikTok LIVE messages received while connected. This is not a complete stream archive." },
      { key: "suggestion", text: "An AI answer based on the recent sample that you can choose to save." },
      { key: "history", text: "Up to 30 temporary session answers; the last four pairs help continue the conversation." },
      { key: "questions", text: "Groups detected by text rules, with a message count and pending or answered state." },
      { key: "pulse", text: "Metrics and trends from viewer and message samples for the connected platform." }
    ] },
    { type: "h2", text: "What persists" },
    { type: "para", text: "Notes, ideas, questions with evidence, moments, and summaries stay in SQLite. Answer history is temporary and is cleared when you disconnect. A question marked as answered reflects a user action, not automatic verification of spoken audio." },
    { type: "h2", text: "Multiple connected platforms" },
    { type: "para", text: "Conversation combines messages from active connections. Notes, ideas, questions, and the summary belong to one Rocky session, which remains open until every platform disconnects. Connect one platform at a time if you need separate records and summaries for each stream." },
    { type: "h2", text: "Session time" },
    { type: "para", text: "Moments and stream-related records use elapsed time since the connection began. This time is not necessarily the timestamp in the published video." }
  ]
};

export default content;
