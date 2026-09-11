package dev.rocky.platform.desktop

import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import java.util.prefs.Preferences

object AiDesktopPreferences {
    private val preferences = Preferences.userRoot().node("dev/rocky/ai")

    var automaticAnalysis: Boolean
        get() = preferences.getBoolean(AUTOMATIC_ANALYSIS_KEY, false)
        set(value) = preferences.putBoolean(AUTOMATIC_ANALYSIS_KEY, value)

    var configuration: AiProviderConfiguration
        get() {
            val provider = runCatching {
                AiProviderKind.valueOf(preferences.get(PROVIDER_KEY, AiProviderKind.Ollama.name))
            }.getOrDefault(AiProviderKind.Ollama)
            return AiProviderConfiguration(
                provider = provider,
                endpoint = preferences.get(ENDPOINT_KEY, defaultEndpoint(provider)),
                model = preferences.get(MODEL_KEY, defaultModel(provider)),
            )
        }
        set(value) {
            preferences.put(PROVIDER_KEY, value.provider.name)
            preferences.put(ENDPOINT_KEY, value.endpoint.trim())
            preferences.put(MODEL_KEY, value.model.trim())
        }

    private fun defaultEndpoint(provider: AiProviderKind): String = when (provider) {
        AiProviderKind.Ollama -> "http://localhost:11434"
        AiProviderKind.OpenAI -> "https://api.openai.com"
        AiProviderKind.OpenRouter -> "https://openrouter.ai/api"
    }

    private fun defaultModel(provider: AiProviderKind): String = when (provider) {
        AiProviderKind.Ollama -> "llama3.2"
        AiProviderKind.OpenAI -> ""
        AiProviderKind.OpenRouter -> "openrouter/free"
    }

    private const val PROVIDER_KEY = "provider"
    private const val ENDPOINT_KEY = "endpoint"
    private const val MODEL_KEY = "model"
    private const val AUTOMATIC_ANALYSIS_KEY = "automaticAnalysis"
}
