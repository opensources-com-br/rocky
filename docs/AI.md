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

Use **Save configuration and key** to save the key in macOS Keychain or with user-scoped Windows DPAPI. Unsaved edits stay in memory. **Delete key** removes the saved credential. Legacy plaintext preferences are migrated and removed. Requests use the [Responses API](https://developers.openai.com/api/reference/resources/responses/methods/create) with `store: false`.

## OpenRouter

1. Create an API key in the [OpenRouter settings](https://openrouter.ai/settings/keys).
2. In **Settings → AI**, select **OpenRouter**.
3. Keep `https://openrouter.ai/api` as the endpoint and `openrouter/free` as the model.
4. Paste the API key and select **Test connection**.

`openrouter/free` automatically selects a compatible free model. Availability, the selected model, and limits may vary. Selected messages are processed by OpenRouter and the chosen model provider. The same secure storage controls apply to OpenRouter.

## Suggestion behavior

- **Analyze now** works after at least one real Twitch message arrives.
- Automatic analysis starts disabled. Once enabled, it follows the agent's configured frequency and requires three additional messages.
- All chat requests use up to 200 received messages from the last two minutes, with up to 300 characters per message. This is a limited sample, not a complete stream history. Connection gaps are shown in Conversation.
- Chat is labeled as untrusted content. A generated suggestion must cite message IDs present in the request or Rocky rejects it.
- Only one analysis runs at a time. Rocky never falls back from Ollama to OpenAI automatically.
- Typed requests require no microphone. In-progress analysis can be cancelled. Testing a connection also generates a short response using synthetic messages; API providers may charge for this request.
- Saving a generated suggestion uses the existing local SQLite notes and Markdown export.

API providers receive the selected chat content. Review the provider's data controls before enabling automatic analysis.

Remote API endpoints require HTTPS. HTTP is accepted only for loopback Ollama. Changing provider or endpoint clears the current key; enter the correct key and save again.

Conversation shows the last request duration and reported token totals. Totals are partial: failed/cancelled requests, connection tests, and unsuccessful retry attempts may not include usage. OpenRouter can try up to three responses when the format is invalid. These counters are not a billing estimate; cancellation does not reverse provider charges.
