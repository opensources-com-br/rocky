package dev.rocky.data.voice

import kotlinx.serialization.json.Json
import kotlin.test.*

class ElevenLabsCatalogTest {
    @Test fun followsEncodedPagesAndDeduplicatesVoices() {
        val paths = mutableListOf<String>()
        val catalog = readElevenLabsCatalog { path ->
            paths += path
            Json.parseToJsonElement(when (paths.size) {
                1 -> """{"voices":[{"voice_id":"a","name":"Alpha"}],"has_more":true,"next_page_token":"a+b &"}"""
                2 -> """{"voices":[{"voice_id":"a","name":"Alpha"},{"voice_id":"b","name":"Beta"}],"has_more":false}"""
                else -> """[{"model_id":"tts","name":"Speech","can_do_text_to_speech":true},{"model_id":"stt","name":"Listen","can_do_text_to_speech":false}]"""
            })
        }
        assertEquals(listOf("a", "b"), catalog.voices.map { it.id })
        assertEquals(listOf("tts"), catalog.models.map { it.id })
        assertEquals("/v2/voices?page_size=100&next_page_token=a%2Bb+%26", paths[1])
        assertEquals("/v1/models", paths.last())
    }
    @Test fun refusesIncompletePaginationInsteadOfReturningAPartialCatalog() {
        for (token in listOf("", ",\"next_page_token\":\"\"")) {
            assertFailsWith<IllegalArgumentException> {
                readElevenLabsCatalog {
                    Json.parseToJsonElement("""{"voices":[],"has_more":true$token}""")
                }
            }
        }
    }
    @Test fun capsCatalogRequestsWhenTheProviderKeepsReturningPages() {
        var calls = 0
        assertFailsWith<IllegalStateException> {
            readElevenLabsCatalog {
                calls++
                Json.parseToJsonElement("""{"voices":[],"has_more":true,"next_page_token":"next"}""")
            }
        }
        assertEquals(10, calls)
    }
}
