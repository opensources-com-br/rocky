package dev.rocky.ui.window

import dev.rocky.core.locale.RockyLanguage

internal fun compactMetric(value: Int, language: RockyLanguage): String {
    if (value < 1_000) return value.toString()
    val million = value >= 999_950
    val divisor = if (million) 1_000_000 else 1_000
    val roundedTenths = (value.toLong() * 10 + divisor / 2) / divisor
    val whole = roundedTenths / 10
    val remainder = roundedTenths % 10
    val number = if (remainder == 0L) whole.toString() else "$whole.$remainder"
    val suffix = when {
        language == RockyLanguage.PortugueseBrazil && million -> "mi"
        language == RockyLanguage.PortugueseBrazil -> "mil"
        million -> "M"
        else -> "K"
    }
    return if (language == RockyLanguage.PortugueseBrazil) "$number $suffix" else "$number$suffix"
}
