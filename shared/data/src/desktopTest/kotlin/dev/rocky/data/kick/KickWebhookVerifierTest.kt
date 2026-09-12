package dev.rocky.data.kick

import java.security.KeyPairGenerator
import java.security.Signature
import java.time.Instant
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KickWebhookVerifierTest {
    @Test fun acceptsAuthenticCurrentWebhookAndRejectsChanges() {
        val keys = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val pem = "-----BEGIN PUBLIC KEY-----\n" +
            Base64.getMimeEncoder().encodeToString(keys.public.encoded) +
            "\n-----END PUBLIC KEY-----"
        val verifier = KickWebhookVerifier(pem)
        val timestamp = "2026-09-12T12:00:00Z"
        val body = """{"message_id":"01ABC"}""".toByteArray()
        val signed = "01ABC.$timestamp.".toByteArray() + body
        val signature = Signature.getInstance("SHA256withRSA").run {
            initSign(keys.private); update(signed); Base64.getEncoder().encodeToString(sign())
        }
        val now = Instant.parse("2026-09-12T12:01:00Z")
        assertTrue(verifier.verify("01ABC", timestamp, body, signature, now))
        assertFalse(verifier.verify("01ABC", timestamp, "changed".toByteArray(), signature, now))
        assertFalse(verifier.verify("01ABC", timestamp, body, signature,
            Instant.parse("2026-09-12T12:11:00Z")))
    }
}
