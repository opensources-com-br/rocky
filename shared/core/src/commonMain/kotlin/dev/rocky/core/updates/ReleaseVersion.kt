package dev.rocky.core.updates

data class AvailableUpdate(val version: String, val url: String)

data class ReleaseVersion(val major: Int, val minor: Int, val patch: Int, val alpha: Int?) : Comparable<ReleaseVersion> {
    override fun compareTo(other: ReleaseVersion): Int = compareValuesBy(this, other,
        { it.major }, { it.minor }, { it.patch }, { it.alpha ?: Int.MAX_VALUE })
    companion object {
        fun parse(value: String): ReleaseVersion? {
            val match = Regex("""^v?(\d+)\.(\d+)\.(\d+)(?:-alpha\.(\d+))?$""").matchEntire(value) ?: return null
            val groups = match.groupValues
            return ReleaseVersion(groups[1].toIntOrNull() ?: return null, groups[2].toIntOrNull() ?: return null,
                groups[3].toIntOrNull() ?: return null, if (groups[4].isEmpty()) null else groups[4].toIntOrNull() ?: return null)
        }
    }
}
