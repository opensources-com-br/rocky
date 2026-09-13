package dev.rocky.data.updates

import java.nio.file.Files
import kotlin.test.*

class UpdateIntegrityTest {
    @Test fun requiresExactlyOneChecksumForTheSelectedPackage() {
        val hash = "a".repeat(64)
        assertEquals(hash, expectedChecksum("$hash  Rocky.msi\n", "Rocky.msi"))
        assertEquals(hash, expectedChecksum("$hash *Rocky.msi\n", "Rocky.msi"))
        assertFails { expectedChecksum("$hash  another.msi", "Rocky.msi") }
        assertFails { expectedChecksum("$hash  Rocky.msi\n$hash  Rocky.msi", "Rocky.msi") }
        assertFails { expectedChecksum("invalid  Rocky.msi", "Rocky.msi") }
    }
}
