# AI providers

Rocky can generate grounded suggestions from Twitch chat with a local Ollama model or the OpenAI Responses API.

## Ollama

1. [Install Ollama](https://ollama.com/download) and keep it running.
2. Download the default model with `ollama pull llama3.2`, or choose another installed text model.
3. In Rocky, open **Settings → AI** and select **Ollama local**.
4. Keep `http://localhost:11434` as the endpoint, enter the installed model name, and select **Test connection**.

Messages stay on the computer when the Ollama endpoint is local. Rocky does not install or bundle models.

## OpenAI API

1. Create an API key for your OpenAI API project. A ChatGPT subscription does not supply an API key or API usage credits.
2. In **Settings → AI**, select **OpenAI API**.
3. Keep `https://api.openai.com` as the endpoint, enter a model available to the project, and paste the API key.
4. Select **Test connection**.

The API key remains in memory and is discarded when Rocky closes. It is never saved to preferences. Requests use the [Responses API](https://developers.openai.com/api/reference/resources/responses/methods/create) with `store: false`.

## OpenRouter

1. Create an API key in the [OpenRouter settings](https://openrouter.ai/settings/keys).
2. In **Settings → AI**, select **OpenRouter**.
3. Keep `https://openrouter.ai/api` as the endpoint and `openrouter/free` as the model.
4. Paste the API key and select **Test connection**.

`openrouter/free` automatically selects a compatible free model. Availability, the selected model, and limits may vary. Selected messages are processed by OpenRouter and the chosen model provider. The key remains only in Rocky's memory.

## Suggestion behavior

- **Analyze now** works after at least one real Twitch message arrives.
- Automatic analysis starts disabled. Once enabled, it follows the agent's configured frequency and requires three additional messages; a pending suggestion prevents another automatic analysis.
- A request contains at most the latest 30 messages and 300 characters from each message.
- Chat is labeled as untrusted content. A generated suggestion must cite message IDs present in the request or Rocky rejects it.
- Only one analysis runs at a time. Rocky never falls back from Ollama to OpenAI automatically.
- Typed requests require no microphone. In-progress analysis can be cancelled. Testing a connection also generates a short response using synthetic messages; API providers may charge for this request.
- Saving a generated suggestion uses the existing local SQLite notes and Markdown export.

API providers receive the selected chat content. Review the provider's data controls before enabling automatic analysis.
