package dev.rocky.core.updates

data class ReleaseAsset(val name: String, val url: String, val size: Long)

data class PreparedUpdate(val version: String, val path: String, val sha256: String)

interface UpdateInstaller {
    fun download(update: AvailableUpdate, onProgress: (Long, Long) -> Unit): PreparedUpdate
    fun cancel()
    /** Returns after the updater is ready and waiting for the application to exit. */
    fun open(update: PreparedUpdate)
}
