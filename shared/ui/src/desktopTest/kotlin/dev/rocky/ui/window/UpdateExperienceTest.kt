package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.core.updates.*
import dev.rocky.ui.theme.RockyTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UpdateExperienceTest {
    @get:Rule val rule = createComposeRule()
    private class Installer : UpdateInstaller {
        var opened = 0
        var onDownload: () -> Unit = {}
