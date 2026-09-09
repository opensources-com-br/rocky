package dev.rocky.core.live

object SimulatedLiveScript {
    private val messages = listOf(
        ChatMessage("msg-1", "bia.dev", "Qual é o preço do curso?", StreamPlatform.Twitch),
        ChatMessage("msg-2", "lucas_codes", "Você pode falar quanto custa?", StreamPlatform.YouTube),
        ChatMessage("msg-3", "marina", "O valor do curso está no site?", StreamPlatform.Twitch),
        ChatMessage("msg-4", "rafa.kt", "Tem desconto para quem está na live?", StreamPlatform.Kick),
        ChatMessage("msg-5", "camila", "Quanto fica o curso à vista?", StreamPlatform.Twitch),
        ChatMessage("msg-6", "joao.front", "Perdi o preço, pode repetir?", StreamPlatform.YouTube),
        ChatMessage("msg-7", "nina", "Também queria saber o valor.", StreamPlatform.Twitch),
    )

    val events: List<TimedLiveEvent> = buildList {
        messages.forEachIndexed { index, message ->
            add(
                TimedLiveEvent(
                    delayMillis = if (index == 0) 600 else 850,
                    event = LiveEvent.MessageReceived(message),
                ),
            )
        }
        add(
            TimedLiveEvent(
                delayMillis = 700,
                event = LiveEvent.SuggestionCreated(
                    RockySuggestion(
                        id = "suggestion-course-price",
                        text = "Sete pessoas perguntaram o preço do curso nos últimos dois minutos. Vale responder agora.",
                        sourceMessageIds = messages.mapTo(linkedSetOf()) { it.id },
                    ),
                ),
            ),
        )
    }
}
