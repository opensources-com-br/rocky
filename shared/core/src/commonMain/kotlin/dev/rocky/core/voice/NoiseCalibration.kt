package dev.rocky.core.voice

class NoiseCalibration {
    private val samples = mutableListOf<Float>()
    fun sample(level: Float) { if (level.isFinite() && samples.size < 100) samples += level.coerceIn(0f, 1f) }
    fun threshold(): Float {
        require(samples.size >= 15) { "Amostras insuficientes para calibrar" }
        val sorted = samples.sorted()
        val noise = sorted[(sorted.size * 0.9).toInt().coerceAtMost(sorted.lastIndex)]
        require(noise < 0.1f) { "Ambiente muito alto. Reduza o ruído e tente novamente." }
        return (noise * 2.5f + 0.005f).coerceIn(0.01f, 0.15f)
    }
}
