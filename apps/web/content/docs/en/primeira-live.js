const content = {
  group: "Getting started",
  title: "First stream",
  lead: "Start with the first-use assistant or Settings. Voice can be prepared later; quick analysis actions do not require a microphone.",
  blocks: [
    { type: "h2", text: "Prepare the session" },
    { type: "list", items: [
      "Under Platforms, connect Twitch, Kick, YouTube, Facebook, or TikTok LIVE. TikTok only needs the username and an active public live; Kick requires forwarding through a public HTTPS webhook; YouTube and Facebook require app credentials and an active stream with chat.",
      "Under AI, choose Ollama, OpenAI API, Anthropic API, Google Gemini API, or OpenRouter, enter the model, and test the connection. Explicitly save the configuration and key when applicable.",
      "Receive new messages on the authenticated channel. Use Analyze now or Top questions, What did I miss?, and Chat ideas on the Conversation tab. The typed question field is hidden in the current layout.",
      "If you want voice, configure whisper-cli, the transcription model, and the microphone; then test the voice conversation.",
      "Open Before stream to check the platform, AI, and optional audio. Make a test recording in OBS to verify what the audience will hear and see."
    ] },
    { type: "h2", text: "Interventions" },
    { type: "para", text: "Automatic analysis starts disabled. Under AI, choose On demand, Discreet, or Proactive. Automatic profiles have minimum intervals of five and two minutes and require new messages; direct questions take priority." },
    { type: "h2", text: "Default shortcuts" },
    { type: "list", items: [
      "Ctrl + Shift + F8: capture a direct command without saying Rocky.",
      "Ctrl + Shift + F9: mute or resume voice.",
      "Ctrl + Shift + F10: show or hide the window.",
      "On Mac, use Control; some keyboards also require Fn. Under Voice, choose three distinct keys between F1 and F12."
    ] },
    { type: "h2", text: "Window" },
    { type: "para", text: "The expanded window position and size are persisted. Use compact mode and always-on-top controls as needed for your stream." }
  ]
};

export default content;
