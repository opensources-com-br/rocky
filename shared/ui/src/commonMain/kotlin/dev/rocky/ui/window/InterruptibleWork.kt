package dev.rocky.ui.window

internal expect suspend fun <T> interruptibleWork(block: () -> T): T
