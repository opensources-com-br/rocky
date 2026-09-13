package dev.rocky.ui.window

internal class ConversationTurns(private val nowMillis: () -> Long) {
    private var generation = 0L
    private var followUpUntil = 0L
    private var wakeUntil = 0L
    fun begin(): Long { wakeUntil = 0; followUpUntil = 0; return ++generation }
    fun accepts(turn: Long) = turn == generation
    fun finish(turn: Long) { if (accepts(turn)) followUpUntil = nowMillis() + 12_000 }
    fun awaitCommand() { wakeUntil = nowMillis() + 8_000 }
    fun reset() { begin() }
    fun awaitingCommand() = wakeUntil > nowMillis()
    fun acceptsFollowUp(text: String): Boolean = nowMillis() < followUpUntil && Regex(
        """^(?:explica(?: melhor)?|explique|resume|resuma|repete|repita|salva|salve|anota|anote|e (?:sobre|quanto)|explain|summarize|repeat|save|what about)\b.*""",
        RegexOption.IGNORE_CASE,
    ).matches(text.trim())
}
