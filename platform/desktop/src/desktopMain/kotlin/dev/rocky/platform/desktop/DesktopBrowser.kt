package dev.rocky.platform.desktop

import java.awt.Desktop
import java.net.URI

fun openInBrowser(url: String): Boolean = runCatching {
    check(Desktop.isDesktopSupported())
    val desktop = Desktop.getDesktop()
    check(desktop.isSupported(Desktop.Action.BROWSE))
    desktop.browse(URI.create(url))
}.isSuccess
