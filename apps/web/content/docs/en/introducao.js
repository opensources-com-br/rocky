const content = {
  group: "Getting started",
  title: "Introduction",
  lead: "Rocky is a desktop assistant for macOS and Windows. The current version follows new messages from your Twitch, Kick, YouTube, Facebook, or TikTok LIVE channel and helps you check chat, organize questions, and save records.",
  blocks: [
    { type: "h2", text: "What works today" },
    { type: "list", items: [
      "Quick analysis actions and, with recognition configured, free-form voice questions.",
      "Suggestions through Ollama, the OpenAI API, the Anthropic API, the Google Gemini API, the xAI Grok API, or OpenRouter, using a recent chat sample.",
      "A grouped question queue, session history, notes, ideas, moments, and a record summary.",
      "A resizable window, compact mode, and an option to keep it on top."
    ] },
    { type: "h2", text: "Current version limits" },
    { type: "para", text: "This is an alpha version; check the notes and installers in the official release. Super Chats, Stars, gifts, message sending, and plugins are not integrated. Kick requires a public HTTPS webhook; Facebook requires a Business app and a Page with an active live video; TikTok uses unofficial WebCast through Eulerstream. The app does not moderate chat or record the stream." },
    { type: "h2", text: "Audio and privacy" },
    { type: "para", text: "The app does not require a Rocky account or include its own backend. Every platform requires internet access; Kick also needs HTTPS forwarding. Remote AI providers receive the request and chat sample. When selected under Voice, ElevenLabs receives the text to be spoken and may use your account credits. Check your OBS sources with a test recording." },
    { type: "h2", text: "Open source" },
    { type: "para", text: "The project is distributed under the MIT license. The interface is available in English and Brazilian Portuguese." }
  ]
};

export default content;
