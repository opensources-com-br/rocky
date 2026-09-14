const content = {
  group: "Configuration",
  title: "AI model",
  lead: "Choose the provider under Settings → AI. The current integration offers local Ollama, the OpenAI API, the Anthropic API, and OpenRouter.",
  blocks: [
    { type: "h2", text: "Ollama" },
    { type: "para", text: "Keep the server running with a text model installed. Use http://localhost:11434 and enter the exact model name; the app default is llama3.2. Rocky does not install AI models. Use Find models and Choose model, or enter the name manually." },
    { type: "h2", text: "Remote APIs" },
    { type: "para", text: "OpenAI uses https://api.openai.com and the Responses API. Anthropic uses https://api.anthropic.com and the Messages API; its initial model is claude-haiku-4-5-20251001. OpenRouter uses https://openrouter.ai/api; the default model is openrouter/free, whose availability varies. Enter a key and a model available to your account. There is no generic OpenAI-compatible integration." },
    { type: "h2", text: "Test and save" },
    { type: "para", text: "Test the connection before your stream. The test makes a short generation and may consume provider usage. Saving the configuration and key stores the credential in macOS Keychain or protected by DPAPI on Windows; unsaved edits stay in memory. Changing provider or endpoint clears the current key." },
    { type: "h2", text: "Context and continuity" },
    { type: "para", text: "Analysis uses up to 200 messages received in the last two minutes, with up to 300 characters per message. The last four question-and-answer pairs help maintain continuity. There is no configurable ten-minute or full-stream window. Cited sources must belong to the submitted sample." },
    { type: "h2", text: "Usage and privacy" },
    { type: "para", text: "For local processing, select a local Ollama model; a loopback address alone does not prove where the server runs the model. OpenAI, Anthropic, and OpenRouter receive the request and chat sample; OpenRouter also forwards them to the model provider. Token totals are partial, not a billing estimate. Canceling does not reverse usage already incurred." }
  ]
};

export default content;
