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

