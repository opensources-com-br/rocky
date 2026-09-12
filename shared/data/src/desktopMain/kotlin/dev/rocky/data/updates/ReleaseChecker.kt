package dev.rocky.data.updates

import dev.rocky.core.updates.*
import java.net.URI
import java.net.http.*
import java.time.Duration
import kotlinx.serialization.json.*

class ReleaseChecker {
    fun check(current: String): AvailableUpdate? {
        val version = ReleaseVersion.parse(current) ?: error("Build sem versão de release.")
        val request = HttpRequest.newBuilder(URI("https://api.github.com/repos/opensources-com-br/rocky/releases?per_page=30"))
            .timeout(Duration.ofSeconds(10)).header("Accept", "application/vnd.github+json").header("User-Agent", "Rocky").GET().build()
        val response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
            .send(request, HttpResponse.BodyHandlers.ofInputStream())
        val text = response.body().use {
            require(response.statusCode() == 200) { "Não foi possível consultar as versões oficiais." }
            val bytes = it.readNBytes(1024 * 1024 + 1)
            require(bytes.size <= 1024 * 1024)
            bytes.toString(Charsets.UTF_8)
        }
        return selectUpdate(text, version)
    }
}

internal fun selectUpdate(json: String, current: ReleaseVersion): AvailableUpdate? =
    Json.parseToJsonElement(json).jsonArray.mapNotNull { item ->
        val record = item.jsonObject
        if (record["draft"]?.jsonPrimitive?.booleanOrNull != false) return@mapNotNull null
        val tag = record["tag_name"]?.jsonPrimitive?.content ?: return@mapNotNull null
        val version = ReleaseVersion.parse(tag) ?: return@mapNotNull null
        if (version <= current || (current.alpha == null && version.alpha != null)) return@mapNotNull null
        version to AvailableUpdate(tag, "https://github.com/opensources-com-br/rocky/releases/tag/$tag")
    }.maxByOrNull { it.first }?.second
