package dev.rocky.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import dev.rocky.core.locale.RockyLanguage

internal val LocalRockyLanguage = staticCompositionLocalOf { RockyLanguage.English }

@Composable
internal fun tr(english: String, portugueseBrazil: String): String =
    if (LocalRockyLanguage.current == RockyLanguage.PortugueseBrazil) portugueseBrazil else english
