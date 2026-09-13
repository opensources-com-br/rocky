package dev.rocky.data.updates

import dev.rocky.core.updates.*
import java.nio.file.*

class UpdateDownloader(private val directory: Path) {
    @Volatile private var transfer: UpdateTransfer? = null

    fun cancel() { transfer?.cancel() }

}
