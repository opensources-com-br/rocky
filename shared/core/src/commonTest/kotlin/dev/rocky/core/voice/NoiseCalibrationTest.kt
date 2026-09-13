package dev.rocky.core.voice

import kotlin.test.*

class NoiseCalibrationTest {
    @Test fun handlesSilenceAndOccasionalPeaks() {
        val calibration = NoiseCalibration()
        repeat(39) { calibration.sample(0f) }
        calibration.sample(1f)
        assertEquals(0.01f, calibration.threshold())
    }
    @Test fun rejectsLoudOrInsufficientSamples() {
        val calibration = NoiseCalibration()
        assertFailsWith<IllegalArgumentException> { calibration.threshold() }
        repeat(40) { calibration.sample(0.2f) }
        assertFailsWith<IllegalArgumentException> { calibration.threshold() }
    }
    @Test fun separatedClicksDoNotAccumulateIntoSpeech() {
        val detector = SpeechEndpointDetector()
        repeat(20) { detector.sample(0.2f, 75); detector.sample(0f, 225) }
        assertFalse(detector.heardSpeech)
    }
}
