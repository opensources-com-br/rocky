package dev.rocky.core.live

import kotlin.test.*

class ChatContextFilterTest {
    private fun msg(id: String, author: String, text: String) =
        ChatMessage(id, author, text, StreamPlatform.Twitch)

    @Test fun removesBotsCommandsAndRepeatedSpamWithoutLosingConsensus() {
        val input = listOf(msg("1", "NightBot", "welcome"), msg("2", "a", "!discord"),
            msg("3", "a", "Qual jogo?"), msg("4", "a", "Qual jogo?"),
            msg("5", "b", "Qual jogo?"), msg("6", "c", "aaaaaaaaaaaaaa"))
        val result = filterChat(input, ChatFilterConfiguration())
        assertEquals(listOf("3", "5"), result.messages.map { it.id })
        assertEquals(4, result.removed)
    }

    @Test fun allowsOptingOutAndLimitsOneAuthorsFlood() {
        val input = (1..20).map { msg("$it", "a", "question $it?") }
        assertEquals(10, filterChat(input, ChatFilterConfiguration()).messages.size)
        assertEquals(20, filterChat(input, ChatFilterConfiguration(repetitions = false)).messages.size)
        val commands = listOf(msg("1", "nightbot", "!help"))
        assertEquals(commands, filterChat(commands, ChatFilterConfiguration(false, false, emptySet())).messages)
    }

    @Test fun keepsNewQuestionsAfterOldActivityAndExpiresRepeatedText() {
        val input = (1..11).map { msg("$it", "viewer", "Pergunta $it?")
            .copy(receivedAtMillis = it * 180_000L) }
        assertEquals(input, filterChat(input, ChatFilterConfiguration()).messages)
        val repeated = listOf(0L, 29_999L, 60_000L).mapIndexed { index, time ->
            msg("$index", "viewer", "Qual jogo?").copy(receivedAtMillis = time)
        }
        assertEquals(listOf("0", "2"), filterChat(repeated, ChatFilterConfiguration()).messages.map { it.id })
    }

    @Test fun limitsTimedBurstsAndReleasesTheBoundary() {
        val burst = (0..10).map { msg("$it", "viewer", "Pergunta $it?").copy(receivedAtMillis = 0) }
        val next = msg("new", "viewer", "Outra pergunta?").copy(receivedAtMillis = 30_000)
        val kept = filterChat(burst + next, ChatFilterConfiguration()).messages
        assertEquals(11, kept.size)
        assertEquals("new", kept.last().id)
        assertFalse(kept.any { it.id == "10" })
    }

    @Test fun groupsRewordedQuestionsConservatively() {
        assertTrue(isChatQuestion("Qual o próximo jogo"))
        assertFalse(isChatQuestion("gostei da live"))
        assertTrue(similarQuestion("Qual é o próximo jogo?", "Qual próximo jogo?"))
        assertFalse(similarQuestion("Qual próximo jogo?", "Quanto custa seu computador?"))
    }
}
