package dev.rocky.data.kick

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.time.Instant
import java.util.Base64

internal class KickWebhookVerifier(publicKeyPem: String) {
    private val publicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(
        Base64.getMimeDecoder().decode(publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")),
    ))

    fun verify(
        messageId: String,
        timestamp: String,
        body: ByteArray,
        signature: String,
        now: Instant = Instant.now(),
    ): Boolean {
        val sentAt = runCatching { Instant.parse(timestamp) }.getOrNull() ?: return false
        if (Duration.between(sentAt, now).abs() > Duration.ofMinutes(10)) return false
        val signed = "$messageId.$timestamp.".toByteArray() + body
        return runCatching {
            Signature.getInstance("SHA256withRSA").apply {
                initVerify(publicKey)
                update(signed)
            }.verify(Base64.getDecoder().decode(signature))
        }.getOrDefault(false)
    }
}
