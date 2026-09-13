package dev.rocky.platform.desktop

object ElevenLabsPreferences {
    private var cached: String? = null
    var notice: String? = null; private set
    fun readKey(): String {
        cached?.let { return it }
        return try { desktopSecretStore("elevenlabs").read().orEmpty().also { cached = it } }
        catch (_: Exception) { notice = "Não foi possível ler a chave ElevenLabs no cofre."; "" }
        catch (_: LinkageError) { notice = "Cofre indisponível para ElevenLabs."; "" }
    }
    fun saveKey(value: String) {
        if (value == cached) return
        check(value.isNotBlank() || notice == null) { "Cofre indisponível; a chave existente foi preservada." }
        val store = desktopSecretStore("elevenlabs")
        if (value.isBlank()) store.delete() else store.write(value.trim())
        cached = value.trim(); notice = null
    }
    fun clear() { desktopSecretStore("elevenlabs").delete(); cached = ""; notice = null }
}
