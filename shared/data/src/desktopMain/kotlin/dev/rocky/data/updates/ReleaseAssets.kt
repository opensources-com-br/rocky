package dev.rocky.data.updates

import dev.rocky.core.updates.*
import kotlinx.serialization.json.*

internal const val RELEASE_DOWNLOAD = "https://github.com/opensources-com-br/rocky/releases/download/"

internal fun releaseAssets(record: JsonObject, tag: String): List<ReleaseAsset> =
    (record["assets"] as? JsonArray).orEmpty().mapNotNull { item ->
        val asset = item as? JsonObject ?: return@mapNotNull null
        val name = asset["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
        val size = asset["size"]?.jsonPrimitive?.longOrNull ?: return@mapNotNull null
        if (!Regex("[A-Za-z0-9_.-]+").matches(name) || size <= 0) return@mapNotNull null
        val url = "$RELEASE_DOWNLOAD$tag/$name"
        if (asset["browser_download_url"]?.jsonPrimitive?.content != url) return@mapNotNull null
        ReleaseAsset(name, url, size)
    }

fun installerAsset(update: AvailableUpdate, os: String, arch: String): ReleaseAsset {
    val system = when {
        os.lowercase().startsWith("windows") -> "windows"
        os.lowercase().startsWith("mac") -> "darwin"
        else -> error("Atualização disponível apenas para macOS e Windows.")
    }
    val architectures = when (arch.lowercase()) {
        "aarch64", "arm64" -> setOf("arm64", "aarch64")
        "x86_64", "amd64", "x64" -> setOf("x86_64", "amd64")
        else -> error("Arquitetura sem instalador compatível.")
    }
    val extension = if (system == "darwin") "dmg" else "msi"
    val version = update.version.removePrefix("v")
    val names = architectures.map { "Rocky-$version-$system-$it.$extension" }
    return update.assets.singleOrNull { it.name in names }
        ?: error("Esta versão não possui um instalador compatível com este computador.")
}
