package work.msdnicrosoft.mirai.plugin.hitokoto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HitokotoResponse(
    val hitokoto: String,
    val from: String,

    @SerialName("from_who")
    val fromWho: String?,
)
