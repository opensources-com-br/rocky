package dev.rocky.data.updates

import dev.rocky.core.updates.PreparedUpdate
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*

class UpdateHelperLauncherTest {
    private class RunningProcess(private val running: Boolean = true) : Process() {
        override fun getOutputStream() = ByteArrayOutputStream()
        override fun getInputStream() = "".byteInputStream()
        override fun getErrorStream() = "".byteInputStream()
        override fun waitFor() = 0
        override fun exitValue() = 0
        override fun destroy() = Unit
        override fun isAlive() = running
        override fun pid() = 123456789L
    }

