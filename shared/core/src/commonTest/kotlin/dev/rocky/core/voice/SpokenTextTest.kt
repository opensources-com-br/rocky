package dev.rocky.core.voice

import kotlin.test.*

class SpokenTextTest {
    @Test fun preparesFormattingAndPortugueseSpeech() {
        assertEquals("Live às 19 horas e 30 minutos: 50 por cento no O B S. inteligência artificial",
            spokenText("## **Live** às 19:30: 50% no OBS. [IA](https://example.com)", "pt"))
    }
    @Test fun hidesCodeAndExpandsEnglishPercentages() {
        assertEquals("code excerpt 20 percent", spokenText("```js\nalert(1)\n``` 20%", "en"))
    }
    @Test fun preservesOrdinaryWordsAndRejectsInvalidTimes() {
        assertEquals("Hoje às 29:99, tudo bem?", spokenText("Hoje às 29:99, tudo bem?", "pt"))
    }
}
