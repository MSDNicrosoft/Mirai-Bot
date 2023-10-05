package work.msdnicrosoft.mirai.plugin.minecraft.version.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftManifestResponse(
    val latest: Latest,
    val versions: List<Version>
) {
    @Serializable
    data class Latest(
        val release: String,
        val snapshot: String
    )

    @Serializable
    data class Version(
        @SerialName("id")
        val versionName: String,

        @SerialName("type")
        val releaseType: String,

        val url: String,
        val time: String,
        val releaseTime: String,
        val sha1: String,
        val complianceLevel: Int
    )
}
