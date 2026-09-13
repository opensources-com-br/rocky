package dev.rocky.core.voice

fun spokenText(text: String, language: String): String {
    val english = language == "en"
    var result = text.replace(Regex("```[\\s\\S]*?```"), if (english) " code excerpt " else " trecho de código ")
        .replace(Regex("!?\\[([^]]+)]\\([^)]*\\)"), "$1")
        .replace(Regex("https?://\\S+"), if (english) "link" else "link")
        .replace(Regex("(?m)^\\s{0,3}(?:#{1,6}|>|[-*+])\\s+"), "")
        .replace(Regex("[*_`~]"), "")
    result = Regex("\\b([01]?\\d|2[0-3]):([0-5]\\d)\\b").replace(result) {
        val hour = it.groupValues[1].toInt()
        val minute = it.groupValues[2].toInt()
        if (english) "$hour hours and $minute minutes" else "$hour horas e $minute minutos"
    }
    result = result.replace(Regex("(?<=\\d)%"), if (english) " percent" else " por cento")
        .replace(Regex("\\bOBS\\b"), "O B S")
        .replace(Regex("\\bIA\\b"), if (english) "A I" else "inteligência artificial")
    return result.replace(Regex("\\s+"), " ").trim()
}
