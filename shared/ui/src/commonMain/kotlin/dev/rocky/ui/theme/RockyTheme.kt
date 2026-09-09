package dev.rocky.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rocky.ui.resources.Res
import dev.rocky.ui.resources.instrument_serif_regular
import org.jetbrains.compose.resources.Font

object RockyColors {
    val Accent = Color(0xFFE57436)
    val AccentMuted = Color(0xFF7D3D22)
    val Background = Color(0xFF111113)
    val Backdrop = Color(0xFF160E0A)
    val Surface = Color(0xFF171719)
    val SurfaceElevated = Color(0xFF202023)
    val SurfaceSelected = Color(0xFF3A3A3E)
    val Border = Color(0xFF303034)
    val Divider = Color(0xFF29292D)
    val TextPrimary = Color(0xFFF6F4F2)
    val TextSecondary = Color(0xFFA5A1A5)
    val TextMuted = Color(0xFF777479)

    val Twitch = Color(0xFF9347FF)
    val Kick = Color(0xFF53FC18)
    val YouTube = Color(0xFFFF164A)
    val Offline = Color(0xFF4D4D51)
    val WindowClose = Color(0xFFFF5F57)
    val WindowMinimize = Color(0xFFFFBD2E)
    val WindowExpand = Color(0xFF28C840)
}

@Composable
fun RockyTheme(content: @Composable () -> Unit) {
    val displayFamily = FontFamily(Font(Res.font.instrument_serif_regular))
    val sansFamily = FontFamily.SansSerif

    MaterialTheme(
        colors = darkColors(
            primary = RockyColors.Accent,
            primaryVariant = RockyColors.AccentMuted,
            secondary = RockyColors.Accent,
            background = RockyColors.Background,
            surface = RockyColors.Surface,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = RockyColors.TextPrimary,
            onSurface = RockyColors.TextPrimary,
        ),
        typography = Typography(
            h1 = TextStyle(
                fontFamily = displayFamily,
                fontSize = 31.sp,
                lineHeight = 38.sp,
                color = RockyColors.TextPrimary,
            ),
            h5 = TextStyle(
                fontFamily = sansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 26.sp,
            ),
            h6 = TextStyle(
                fontFamily = sansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                lineHeight = 22.sp,
            ),
            subtitle1 = TextStyle(
                fontFamily = sansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 21.sp,
            ),
            body1 = TextStyle(
                fontFamily = sansFamily,
                fontSize = 15.sp,
                lineHeight = 21.sp,
            ),
            body2 = TextStyle(
                fontFamily = sansFamily,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            ),
            button = TextStyle(
                fontFamily = sansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
            caption = TextStyle(
                fontFamily = sansFamily,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
        ),
        shapes = Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        ),
        content = content,
    )
}
