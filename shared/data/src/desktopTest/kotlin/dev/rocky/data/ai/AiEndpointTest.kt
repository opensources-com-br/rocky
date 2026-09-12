package dev.rocky.data.ai

import dev.rocky.core.ai.*
import kotlin.test.*

class AiEndpointTest {
    @Test fun rejectsUnsafeOriginsBeforeTransport() {
        for (endpoint in listOf("http://example.com", "http://localhost", "file:///tmp/key", "https://user:pass@example.com", "https://example.com?key=x")) {
            assertNotNull(AiProviderConfiguration(AiProviderKind.OpenAI, endpoint, "test", "synthetic").validationError())
        }
        assertNull(AiProviderConfiguration(AiProviderKind.OpenAI, "https://api.openai.com", "test", "synthetic").validationError())
        assertNull(AiProviderConfiguration(AiProviderKind.Ollama, "http://127.0.0.1:11434", "test").validationError())
        assertNotNull(AiProviderConfiguration(AiProviderKind.Ollama, "http://192.168.1.10:11434", "test").validationError())
    }
}
