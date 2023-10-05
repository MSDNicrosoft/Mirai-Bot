package work.msdnicrosoft.mirai.plugin.crazythursday.data

import kotlinx.serialization.Serializable

@Serializable
data class CrazyThursdayData(val version: String, val post: List<String>)
