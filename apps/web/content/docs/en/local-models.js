const content = {
  group: "Advanced",
  title: "Local models",
  lead: "Ollama is the implemented local integration. Rocky connects to an existing server; it does not include an AI model.",
  blocks: [
    { type: "h2", text: "Prepare" },
    { type: "para", text: "Install and start Ollama by following its documentation. Download a text model, for example with ollama pull llama3.2. Under AI, select Local Ollama, enter http://localhost:11434 and the model name, then test the connection." },
    { type: "h2", text: "Compatibility" },
    { type: "para", text: "The app uses the Ollama API. LM Studio, llama.cpp, and servers that only provide an OpenAI-compatible API are not supported providers in the current interface." },
    { type: "h2", text: "Machine resources" },
    { type: "para", text: "Memory use and response time depend on the model and hardware. Test alongside the game and encoder before streaming. The documentation does not guarantee performance for a model size at a specific amount of RAM." },
    { type: "h2", text: "What stays local" },
    { type: "para", text: "Use a local Ollama model to keep analysis on your computer; a loopback address alone does not guarantee local execution. Transcription and system voices can also be local after setup. Twitch, Kick, YouTube, Facebook, TikTok LIVE, ElevenLabs, and downloads require internet access; Kick chat requires HTTPS forwarding and TikTok uses the Eulerstream service." }
  ]
};

export default content;
