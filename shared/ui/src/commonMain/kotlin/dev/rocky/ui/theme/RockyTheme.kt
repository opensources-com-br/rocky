package dev.rocky.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RockyColors {
    val Accent = Color(0xFFE87438)
    val Background = Color(0xFF121214)
    val Surface = Color(0xFF1B1B1E)
    val SurfaceElevated = Color(0xFF242428)
    val TextPrimary = Color(0xFFF6F4F2)
    val TextSecondary = Color(0xFFA8A4A8)
}

@Composable
fun RockyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = darkColors(
            primary = RockyColors.Accent,
            background = RockyColors.Background,
            surface = RockyColors.Surface,
            onPrimary = Color.Black,
            onBackground = RockyColors.TextPrimary,
            onSurface = RockyColors.TextPrimary,
        ),
        content = content,
    )
}
