package work.msdnicrosoft.mirai.plugin.blacklist.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object BlacklistConfig: AutoSavePluginConfig("blacklist") {

    @ValueDescription("是否启用黑名单")
    var enabled by value<Boolean>(false)

    val list by value<MutableList<String>>()
}
