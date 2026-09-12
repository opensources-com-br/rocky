package dev.rocky.core.live

private val questionStarts = Regex(
    """^(como|qual|quais|quando|onde|quem|quanto|quantos|por que|porque|what|when|where|who|how|why|can|does|is)\b""",
    RegexOption.IGNORE_CASE,
)
fun isChatQuestion(text: String): Boolean = text.contains('?') || questionStarts.containsMatchIn(text.trim())

private fun questionWords(text: String): Set<String> = text.lowercase()
    .replace(Regex("""[^\p{L}\p{N} ]"""), " ").split(Regex("""\s+"""))
    .filter { it.length > 2 && it !in setOf("que", "qual", "como", "para", "uma", "the", "what", "how", "this") }.toSet()

/** Conservative lexical grouping, not an assertion that two questions have identical meaning. */
fun similarQuestion(first: String, second: String): Boolean {
    if (first.trim().equals(second.trim(), ignoreCase = true)) return true
    val a = questionWords(first); val b = questionWords(second)
    if (a.size < 2 || b.size < 2) return false
    return a.intersect(b).size.toDouble() / a.union(b).size >= 0.6
}

const val QUESTION_TAG = "PERGUNTA"
const val MARKER_TAG = "MOMENTO"
const val SUMMARY_TAG = "RESUMO"
