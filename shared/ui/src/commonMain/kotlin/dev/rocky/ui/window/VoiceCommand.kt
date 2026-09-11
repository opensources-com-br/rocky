package dev.rocky.ui.window

private val rockyWakeWord = Regex(
    pattern = """\b(?:rocky|rocki|roque|roqui|raki|raqui|ráqui)\b""",
    option = RegexOption.IGNORE_CASE,
)

internal fun containsRockyWakeWord(transcript: String): Boolean = rockyWakeWord.containsMatchIn(transcript)

internal fun extractRockyCommand(transcript: String): String? = rockyWakeWord.find(transcript)
    ?.let { transcript.substring(it.range.last + 1) }
    ?.trimStart { it.isWhitespace() || it in ",.:;!?-" }
    ?.trim()
    ?.takeIf(String::isNotEmpty)
