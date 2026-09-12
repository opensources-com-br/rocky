package dev.rocky.core.voice

import kotlin.test.*

class SpeechEndpointDetectorTest {
    @Test fun ignoresNoiseAndWaitsForTrailingSilence() {
        val detector = SpeechEndpointDetector()
        repeat(20) { assertFalse(detector.sample(0f, 75)) }
        assertFalse(detector.heardSpeech)
        assertFalse(detector.sample(0.1f, 75))
        repeat(10) { assertFalse(detector.sample(0f, 75)) }
        repeat(3) { assertFalse(detector.sample(0.1f, 75)) }
        repeat(9) { assertFalse(detector.sample(0f, 75)) }
        assertTrue(detector.sample(0f, 75))
    }
}
