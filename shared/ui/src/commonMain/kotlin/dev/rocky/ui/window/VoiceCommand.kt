package dev.rocky.ui.window

private val rockyWakeWord = Regex(
    pattern = """\b(?:rocky|rocki|roque)\b[\s,.:;!?-]*(.+)""",
    option = RegexOption.IGNORE_CASE,
)

internal fun extractRockyCommand(transcript: String): String? = rockyWakeWord
    .find(transcript)
    ?.groupValues
    ?.get(1)
    ?.trim()
    ?.takeIf(String::isNotEmpty)
