package dev.rocky.platform.desktop

import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.util.prefs.Preferences

object DesktopWindowPreferences {
    private val prefs = Preferences.userRoot().node("dev/rocky/window")

    fun restore(): Rectangle {
        val saved = Rectangle(prefs.getInt("x", Int.MIN_VALUE), prefs.getInt("y", Int.MIN_VALUE),
            prefs.getInt("width", 462), prefs.getInt("height", 900))
        val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.map {
            val configuration = it.defaultConfiguration
            val insets = java.awt.Toolkit.getDefaultToolkit().getScreenInsets(configuration)
            Rectangle(configuration.bounds).apply {
                x += insets.left; y += insets.top
                width -= insets.left + insets.right; height -= insets.top + insets.bottom
            }
        }
        return fitWindow(saved, screens)
    }

    fun save(bounds: Rectangle) {
        if (bounds.width < 340 || bounds.height < 180) return
        prefs.putInt("x", bounds.x); prefs.putInt("y", bounds.y)
        prefs.putInt("width", bounds.width); prefs.putInt("height", bounds.height)
    }

    var pinned: Boolean
        get() = prefs.getBoolean("pinned", false)
        set(value) { prefs.putBoolean("pinned", value) }
}

internal fun fitWindow(saved: Rectangle, screens: List<Rectangle>): Rectangle {
    val screen = screens.firstOrNull { it.contains(saved.x.toDouble(), saved.y.toDouble()) }
        ?: screens.firstOrNull() ?: Rectangle(0, 0, 1280, 800)
    val width = saved.width.coerceIn(340.coerceAtMost(screen.width), screen.width)
    val height = saved.height.coerceIn(180.coerceAtMost(screen.height), screen.height)
    val x = if (saved.x == Int.MIN_VALUE) screen.x + (screen.width - width) / 2 else saved.x
    val y = if (saved.y == Int.MIN_VALUE) screen.y + (screen.height - height) / 2 else saved.y
    return Rectangle(x.coerceIn(screen.x, screen.x + screen.width - width),
        y.coerceIn(screen.y, screen.y + screen.height - height), width, height)
}
