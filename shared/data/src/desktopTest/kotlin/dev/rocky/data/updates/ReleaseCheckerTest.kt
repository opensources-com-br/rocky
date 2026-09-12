package dev.rocky.data.updates

import dev.rocky.core.updates.ReleaseVersion
import kotlin.test.*

class ReleaseCheckerTest {
    private fun releases(vararg tags: String) = tags.joinToString(",", "[", "]") {
        """{"tag_name":"$it","draft":false,"html_url":"https://untrusted.example"}"""
    }
    @Test fun respectsStableAndAlphaChannelsAndOfficialDestination() {
        val stable = ReleaseVersion.parse("1.0.11")!!
        assertNull(selectUpdate(releases("1.0.11", "1.0.12-alpha.1"), stable))
        val update = selectUpdate(releases("1.0.12", "1.0.13-alpha.1"), stable)!!
        assertEquals("1.0.12", update.version)
        assertEquals("https://github.com/opensources-com-br/rocky/releases/tag/1.0.12", update.url)
        assertEquals("v1.0.12-alpha.10", selectUpdate(releases("v1.0.12-alpha.2", "v1.0.12-alpha.10"),
            ReleaseVersion.parse("1.0.11-alpha.1")!!)!!.version)
    }
    @Test fun rejectsDraftsMalformedTagsAndNumericOverflow() {
        val current = ReleaseVersion.parse("1.0.0")!!
        assertNull(selectUpdate("""[{"tag_name":"9.0.0","draft":true}]""", current))
        assertNull(selectUpdate(releases("https://evil.example", "9.0.0/../../evil"), current))
        assertNull(ReleaseVersion.parse("1.0.0-alpha.999999999999999999"))
        assertTrue(ReleaseVersion.parse("1.0.0")!! > ReleaseVersion.parse("1.0.0-alpha.9")!!)
    }
}
