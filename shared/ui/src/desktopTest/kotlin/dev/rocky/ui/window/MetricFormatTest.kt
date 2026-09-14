package dev.rocky.ui.window

import dev.rocky.core.locale.RockyLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class MetricFormatTest {
    @Test
    fun keepsSmallMetricsUnabridged() {
        assertEquals("999", compactMetric(999, RockyLanguage.PortugueseBrazil))
    }

    @Test
    fun compactsPortugueseMetrics() {
        assertEquals("1.1 mil", compactMetric(1_100, RockyLanguage.PortugueseBrazil))
        assertEquals("10.9 mil", compactMetric(10_900, RockyLanguage.PortugueseBrazil))
        assertEquals("100.9 mil", compactMetric(100_900, RockyLanguage.PortugueseBrazil))
        assertEquals("1 mi", compactMetric(1_000_000, RockyLanguage.PortugueseBrazil))
    }

    @Test
    fun compactsEnglishMetrics() {
        assertEquals("1.1K", compactMetric(1_100, RockyLanguage.English))
        assertEquals("100.9K", compactMetric(100_900, RockyLanguage.English))
        assertEquals("1M", compactMetric(1_000_000, RockyLanguage.English))
    }
}
