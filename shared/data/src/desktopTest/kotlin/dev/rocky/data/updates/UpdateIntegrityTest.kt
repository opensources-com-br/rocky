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
    @Test fun detectsAlteredPackageBytes() {
        val file = Files.createTempFile("rocky-integrity-", ".msi")
        try {
            Files.writeString(file, "abc")
            assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", updateChecksum(file))
            Files.writeString(file, "modified")
            assertNotEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", updateChecksum(file))
        } finally { Files.deleteIfExists(file) }
    }
}
