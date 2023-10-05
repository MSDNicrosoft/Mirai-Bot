package work.msdnicrosoft.mirai.plugin.minecraft.server

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object McServerControlConfig : AutoSavePluginConfig("minecraft-server-control") {
    @ValueDescription("MCSM 相关配置")
    val request by value<McsmConfig>()

    @ValueDescription("""实例名与其 UUID 映射（键为名称，值为 UUID）""")
    val instances by value<MutableMap<String, String>>()
}

@Serializable
data class McsmConfig(

    @ValueDescription("MCSM 前端主机名")
    val host: String = "",

    @ValueDescription("MCSM 用户 API Key")
    val apiKey: String = "",

    @ValueDescription("所操作实例所属守护进程的 UUID")
    val daemonUuid: String = ""
)
