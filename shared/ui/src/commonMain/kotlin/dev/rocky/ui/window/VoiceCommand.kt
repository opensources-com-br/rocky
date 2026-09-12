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

internal enum class VoiceSaveTarget { Note, Idea }

internal fun voiceSaveTarget(command: String): VoiceSaveTarget? {
    val match = Regex(
        """^(?:salva|salve|salvar)\s+isso\s+como\s+(nota[s]?|ideia[s]?)[.!?]*$""",
        RegexOption.IGNORE_CASE,
    ).matchEntire(command.trim()) ?: return null
    return if (match.groupValues[1].startsWith("nota", ignoreCase = true)) VoiceSaveTarget.Note else VoiceSaveTarget.Idea
}

internal const val IDEA_TAG = "IDEIA"
