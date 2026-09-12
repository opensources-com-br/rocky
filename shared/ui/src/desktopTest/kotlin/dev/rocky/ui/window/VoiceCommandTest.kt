package dev.rocky.ui.window

import org.junit.Assert.assertEquals
import org.junit.Test

class VoiceCommandTest {
    @org.junit.Test fun recognizesConfiguredNameWithoutPartialWordMatches() {
        org.junit.Assert.assertEquals("resuma", extractRockyCommand("Aurora, resuma", "Aurora"))
        org.junit.Assert.assertEquals(null, extractRockyCommand("Auroral resuma", "Aurora"))
        org.junit.Assert.assertEquals("hello", extractRockyCommand("A+B, hello", "A+B"))
    }

    @Test
    fun extractsACommandAfterTheWakeWord() {
        assertEquals("o que o chat quer jogar?", extractRockyCommand("Rocky, o que o chat quer jogar?"))
        assertEquals("o pessoal gostou?", extractRockyCommand("Ei Roque: o pessoal gostou?"))
        assertEquals("o que o chat quer jogar?", extractRockyCommand("Raki, o que o chat quer jogar?"))
        assertEquals("o pessoal está gostando?", extractRockyCommand("Raqui, o pessoal está gostando?"))
    }

    @Test
    fun ignoresSpeechWithoutACompleteRockyCommand() {
        assertEquals(null, extractRockyCommand("o pessoal está gostando do jogo"))
        assertEquals(null, extractRockyCommand("Rocky"))
        assertEquals(true, containsRockyWakeWord("Rocky"))
        assertEquals(false, containsRockyWakeWord("o chat está falando"))
    }
}
