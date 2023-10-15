package work.msdnicrosoft.mirai.plugin.minecraft.uuid.data

import kotlinx.serialization.Serializable

object Response {
    @Serializable
    data class UuidResponse(val id: String, val name: String)

    @Serializable
    data class ProfileResponse(
        val id: String,
        val name: String,
        val properties: List<Skin>,
        val profileActions: List<String?>
    ) {
        @Serializable
        data class Skin(
            val name: String,
            val value: String
        )
    }
}
