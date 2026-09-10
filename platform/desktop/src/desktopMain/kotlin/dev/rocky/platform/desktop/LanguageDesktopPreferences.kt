package dev.rocky.platform.desktop

import dev.rocky.core.locale.RockyLanguage
import java.util.Locale
import java.util.prefs.Preferences

object LanguageDesktopPreferences {
    private val preferences = Preferences.userRoot().node("dev/rocky/interface")

    var language: RockyLanguage
        get() = preferences.get(LANGUAGE_KEY, null)?.let { saved ->
            runCatching { RockyLanguage.valueOf(saved) }.getOrNull()
        } ?: if (Locale.getDefault().language == "pt") {
            RockyLanguage.PortugueseBrazil
        } else {
            RockyLanguage.English
        }
        set(value) {
            preferences.put(LANGUAGE_KEY, value.name)
        }

    private const val LANGUAGE_KEY = "language"
}
