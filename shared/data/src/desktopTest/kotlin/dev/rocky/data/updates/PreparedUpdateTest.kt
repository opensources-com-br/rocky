package dev.rocky.data.updates

import dev.rocky.core.updates.PreparedUpdate
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*

class PreparedUpdateTest {
    private fun fixture(test: (Path, PreparedUpdate) -> Unit) {
        val directory = Files.createTempDirectory("rocky-prepared-test-")
        try {
            val staging = Files.createTempDirectory(directory, "download-")
            val file = staging.resolve("Rocky-2.0.0-darwin-arm64.dmg")
            Files.writeString(file, "verified package")
            test(directory, PreparedUpdate("v2.0.0", file.toString(), updateChecksum(file)))
        } finally { directory.toFile().deleteRecursively() }
    }

    private fun validate(directory: Path, update: PreparedUpdate, current: String = "1.0.0") =
        validatePreparedUpdate(directory, update, current, "Mac OS X", "aarch64")
