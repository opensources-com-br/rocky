package dev.rocky.core.live

fun summarizeRecords(records: List<LiveNote>, sessionId: String, label: String): String = buildString {
    val current = records.filter { it.sessionId == sessionId && it.tag != SUMMARY_TAG }
    appendLine("Registros da live · $label")
    appendLine("Resumo somente dos registros locais; não representa toda a transmissão.")
    val notes = current.filter { it.tag != QUESTION_TAG }
    appendLine("\nNotas, ideias e momentos (${notes.size}):")
    if (notes.isEmpty()) appendLine("- Nenhum registro salvo.")
    notes.forEach {
        appendLine("- [${it.tag}${if (it.completed) " · realizada" else ""}] ${it.text}")
    }
    val pending = current.filter { it.tag == QUESTION_TAG && !it.completed }
    appendLine("\nPerguntas pendentes (${pending.size}):")
    if (pending.isEmpty()) appendLine("- Nenhuma pergunta pendente registrada.")
    pending.forEach { appendLine("- ${it.text} (${it.messageCount} mensagens agrupadas)") }
    appendLine("\nPerguntas marcadas como respondidas: ${current.count { it.tag == QUESTION_TAG && it.completed }}")
}

fun momentLabel(offsetMillis: Long): String {
    val seconds = offsetMillis.coerceAtLeast(0) / 1000
    return "${seconds / 3600}:${(seconds / 60 % 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
}
