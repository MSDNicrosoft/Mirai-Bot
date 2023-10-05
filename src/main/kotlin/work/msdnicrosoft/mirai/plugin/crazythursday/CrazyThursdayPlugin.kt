package work.msdnicrosoft.mirai.plugin.crazythursday

import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.crazythursday.data.CrazyThursdayData

object CrazyThursdayPlugin : SimpleCommand(
    MiraiBot,
    "疯狂星期四",
    description = "发送 KFC 疯狂星期四随机文案"
) {
    @Handler
    suspend fun onCommand(ctx: CommandContext) {
        val posts = MiraiBot.getResource("crazy-thursday/post.json")!!.let { text ->
            Json.decodeFromString<CrazyThursdayData>(text).post
        }
        ctx.sender.sendMessage(posts.random())
    }
}
