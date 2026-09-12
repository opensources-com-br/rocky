package dev.rocky.platform.desktop

import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import java.util.prefs.Preferences

object AiDesktopPreferences {
    private val delegate by lazy { SecureAiPreferences(Preferences.userRoot().node("dev/rocky/ai"), desktopSecretStore()) }
    var configuration: AiProviderConfiguration
        get() = delegate.configuration
        set(value) { delegate.configuration = value }
    var automaticAnalysis: Boolean
        get() = delegate.automaticAnalysis
        set(value) { delegate.automaticAnalysis = value }
    val storageNotice: String? get() = delegate.storageNotice
    fun clear() = delegate.clear()
}

internal class SecureAiPreferences(private val preferences: Preferences, private val secrets: SecretStore) {
    var storageNotice: String? = null
        private set
    private var loaded = false
    private var cachedKey = ""

    private fun loadKey(): String {
        if (loaded) return cachedKey
        loaded = true
        val legacy = preferences.get(API_KEY_KEY, "")
        try {
            if (legacy.isNotBlank()) secrets.write(credentialIdentity() + "\n" + legacy)
            val saved = secrets.read().orEmpty()
            cachedKey = if (saved.substringBefore('\n') == credentialIdentity()) saved.substringAfter('\n', "") else ""
        } catch (error: Throwable) {
            if (error !is Exception && error !is LinkageError) throw error
            cachedKey = legacy
            storageNotice = "Cofre indisponível. A chave está apenas na memória; salve novamente quando o cofre estiver disponível."
        } finally {
            runCatching { preferences.remove(API_KEY_KEY); preferences.flush() }.onFailure {
                storageNotice = "Não foi possível remover a chave antiga das preferências. Verifique as permissões da conta do sistema."
            }
        }
        return cachedKey
    }

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
                apiKey = loadKey(),
            )
        }
        set(value) {
            if (value.apiKey.isBlank()) secrets.delete() else secrets.write(credentialIdentity(value.provider.name, value.endpoint.trim()) + "\n" + value.apiKey.trim())
            preferences.put(PROVIDER_KEY, value.provider.name)
            preferences.put(ENDPOINT_KEY, value.endpoint.trim())
            preferences.put(MODEL_KEY, value.model.trim())
            cachedKey = value.apiKey.trim()
            loaded = true
            storageNotice = null
            preferences.remove(API_KEY_KEY)
            preferences.flush()
        }

    fun clear() {
        secrets.delete()
        cachedKey = ""
        loaded = true
        preferences.clear()
        preferences.flush()
        storageNotice = null
    }

    private fun credentialIdentity(
        provider: String = preferences.get(PROVIDER_KEY, AiProviderKind.Ollama.name),
        endpoint: String = preferences.get(ENDPOINT_KEY, defaultEndpoint(AiProviderKind.valueOf(provider))),
    ): String = java.security.MessageDigest.getInstance("SHA-256")
        .digest("$provider|${endpoint.trim().trimEnd('/')}".toByteArray()).joinToString("") { "%02x".format(it) }

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

    private val PROVIDER_KEY = "provider"
    private val ENDPOINT_KEY = "endpoint"
    private val MODEL_KEY = "model"
    private val API_KEY_KEY = "apiKey"
    private val AUTOMATIC_ANALYSIS_KEY = "automaticAnalysis"
}
