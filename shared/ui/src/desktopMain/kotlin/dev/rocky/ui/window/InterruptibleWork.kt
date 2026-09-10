package dev.rocky.ui.window

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible

internal actual suspend fun <T> interruptibleWork(block: () -> T): T =
    runInterruptible(Dispatchers.IO, block)
