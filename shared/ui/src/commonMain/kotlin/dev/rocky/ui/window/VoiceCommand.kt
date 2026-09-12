package dev.rocky.ui.window

private val rockyWakeWord = Regex(
    pattern = """\b(?:rocky|rocki|roque|roqui|raki|raqui|ráqui)\b""",
    option = RegexOption.IGNORE_CASE,
)

private fun wakeWord(agentName: String): Regex = if (agentName.trim().equals("Rocky", ignoreCase = true)) rockyWakeWord
    else Regex("(?<![\\p{L}\\p{N}_])${Regex.escape(agentName.trim().ifBlank { "Rocky" })}(?![\\p{L}\\p{N}_])", RegexOption.IGNORE_CASE)

internal fun containsRockyWakeWord(transcript: String, agentName: String = "Rocky"): Boolean = wakeWord(agentName).containsMatchIn(transcript)

internal fun extractRockyCommand(transcript: String, agentName: String = "Rocky"): String? = wakeWord(agentName).find(transcript)
    ?.let { transcript.substring(it.range.last + 1) }
    ?.trimStart { it.isWhitespace() || it in ",.:;!?-" }
    ?.trim()
    ?.takeIf(String::isNotEmpty)
