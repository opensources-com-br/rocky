package dev.rocky.core.voice

/** Energy gate with a minimum voiced duration and trailing silence; never extends the capture cap. */
class SpeechEndpointDetector(
    private val silenceMillis: Long = 750,
    private val threshold: Float = 0.025f,
) {
    private var voicedMillis = 0L
    private var quietMillis = 0L
    val heardSpeech: Boolean get() = voicedMillis >= 225

    fun sample(level: Float, elapsedMillis: Long): Boolean {
        if (level >= threshold) {
            voicedMillis += elapsedMillis
            quietMillis = 0
        } else {
            quietMillis += elapsedMillis
        }
        return heardSpeech && quietMillis >= silenceMillis
    }
}
