package work.msdnicrosoft.mirai.plugin.minecraft.server.data

import kotlinx.serialization.Serializable

@Serializable
data class InstanceStatusResponse(
    val status: Int,
    val data: Data,
    val time: Long
) {
    @Serializable
    data class Data(
        val instanceUuid: String,
        val started: Int,
        val status: Int,
        val info: Info,
    )

    @Serializable
    data class Info(
        val currentPlayers: Int,
        val maxPlayers: Int,
        val version: String
    )
}

@Serializable
data class OutputResponse(
    val status: Int,
    val data: String,
    val time: Long
)
