package work.msdnicrosoft.mirai.plugin.hitokoto.data

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object HitokotoConfig : AutoSavePluginConfig("hitokoto") {

    @ValueDescription("启用一言定时发送")
    val enabled by value<Boolean>(false)

    @ValueDescription("每天的发送时间")
    val time by value<Time>()

    @ValueDescription("要定时发送的群组")
    val groups by value<MutableList<Long>>()
}

@Serializable
data class Time(val hour: Int = 7, val minute: Int = 30)
