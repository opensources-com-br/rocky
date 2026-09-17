package dev.rocky.data.updates

import dev.rocky.core.updates.*
import kotlin.test.*

class ReleaseAssetsTest {
    private fun release(vararg names: String) = AvailableUpdate("v1.2.0", "", names.map { ReleaseAsset(it, "", 42) })
    @Test fun rejectsAmbiguousNativeInstallers() {
        val name = "Rocky-1.2.0-windows-amd64.msi"

        assertFails { installerAsset(release(name, name), "Windows 11", "amd64") }
    }

    @Test fun acceptsNativeArchitectureAliases() {
        val update = release("Rocky-1.2.0-darwin-x86_64.dmg", "Rocky-1.2.0-windows-arm64.msi")

        assertEquals("Rocky-1.2.0-darwin-x86_64.dmg", installerAsset(update, "macOS", "x64").name)
        assertEquals("Rocky-1.2.0-windows-arm64.msi", installerAsset(update, "Windows 11", "aarch64").name)
    }

    @Test fun selectsNativeArchitectureAndMsiInsteadOfExe() {
        val update = release("Rocky-1.2.0-darwin-arm64.dmg", "Rocky-1.2.0-windows-amd64.msi", "Rocky-1.2.0-windows-amd64.exe")
        assertEquals("Rocky-1.2.0-darwin-arm64.dmg", installerAsset(update, "Mac OS X", "aarch64").name)
        assertEquals("Rocky-1.2.0-windows-amd64.msi", installerAsset(update, "Windows 11", "amd64").name)
        assertFails { installerAsset(update, "Mac OS X", "x86_64") }
        assertFails { installerAsset(update, "Linux", "arm64") }
    }
    @Test fun rejectsAssetsOutsideOfficialRelease() {
        val json = """[{"tag_name":"v1.2.0","draft":false,"assets":[
            {"name":"SHA256SUMS.txt","size":10,"browser_download_url":"https://evil.test/file"},
            {"name":"../bad.msi","size":10,"browser_download_url":"https://evil.test/file"}]}]"""
        assertTrue(selectUpdate(json, ReleaseVersion.parse("1.0.0")!!)!!.assets.isEmpty())
    }
}
