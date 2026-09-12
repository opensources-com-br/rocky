package dev.rocky.core.live

data class ChatFilterConfiguration(
    val commands: Boolean = true,
    val repetitions: Boolean = true,
    val bots: Set<String> = setOf("nightbot", "streamelements", "streamlabs", "moobot"),
)
data class FilteredChat(val messages: List<ChatMessage>, val removed: Int)

fun filterChat(messages: List<ChatMessage>, configuration: ChatFilterConfiguration): FilteredChat {
    val seen = mutableSetOf<Pair<String, String>>()
    val counts = mutableMapOf<String, Int>()
    val bots = configuration.bots.map { it.trim().lowercase() }.toSet()
    val kept = messages.filter { message ->
        val text = message.text.trim()
        val author = message.authorId ?: message.author.lowercase()
        val duplicate = !seen.add(author to text.lowercase())
        val count = (counts[author] ?: 0) + 1
        counts[author] = count
        text.isNotBlank() && message.author.lowercase() !in bots &&
            (!configuration.commands || (!text.startsWith("!") && !text.startsWith("/"))) &&
            (!configuration.repetitions || (!duplicate && count <= 10 &&
                !Regex("""(.)\1{12,}""").containsMatchIn(text)))
    }
    return FilteredChat(kept, messages.size - kept.size)
}
