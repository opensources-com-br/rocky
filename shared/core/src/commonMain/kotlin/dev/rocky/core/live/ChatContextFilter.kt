package dev.rocky.core.live

data class ChatFilterConfiguration(
    val commands: Boolean = true,
    val repetitions: Boolean = true,
    val bots: Set<String> = setOf("nightbot", "streamelements", "streamlabs", "moobot"),
)
data class FilteredChat(val messages: List<ChatMessage>, val removed: Int)

fun filterChat(messages: List<ChatMessage>, configuration: ChatFilterConfiguration): FilteredChat {
    val recentByAuthor = mutableMapOf<String, MutableList<Pair<Long, String>>>()
    val bots = configuration.bots.map { it.trim().lowercase() }.toSet()
    val kept = messages.filter { message ->
        val text = message.text.trim()
        val author = message.authorId ?: message.author.lowercase()
        // Live messages carry receive times. Untimed input is treated as one batch.
        val now = message.receivedAtMillis ?: 0L
        val recent = recentByAuthor.getOrPut(author) { mutableListOf() }
        recent.removeAll { (time, _) -> time <= now - 30_000 }
        val duplicate = recent.any { (_, previous) -> previous == text.lowercase() }
        recent.add(now to text.lowercase())
        val count = recent.size
        text.isNotBlank() && message.author.lowercase() !in bots &&
            (!configuration.commands || (!text.startsWith("!") && !text.startsWith("/"))) &&
            (!configuration.repetitions || (!duplicate && count <= 10 &&
                !Regex("""(.)\1{12,}""").containsMatchIn(text)))
    }
    return FilteredChat(kept, messages.size - kept.size)
}
