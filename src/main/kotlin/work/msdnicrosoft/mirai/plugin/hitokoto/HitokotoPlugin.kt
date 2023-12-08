package work.msdnicrosoft.mirai.plugin.hitokoto

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import org.quartz.Job
import org.quartz.JobExecutionContext
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.hitokoto.data.HitokotoConfig
import work.msdnicrosoft.mirai.plugin.hitokoto.data.Response.HitokotoResponse
import work.msdnicrosoft.mirai.util.NetworkUtil

object HitokotoPlugin : SimpleCommand(
    MiraiBot,
    "hitokoto",
    "一言",
    description = "总有些句子能穿透你的心"
) {
    @Handler
    suspend fun onCommand(ctx: CommandContext) {
        ctx.sender.sendMessage(getHitokoto())
    }
}

suspend fun getHitokoto(): String {
    val client = NetworkUtil.client.config {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
    val resp = client.use {
        it.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "v1.hitokoto.cn"
                parameters {
                    append("encode", "json")
                    append("charset", "utf-8")
                }
            }
        }
    }
    return if (resp.status.isSuccess()) {
        val data = resp.body<HitokotoResponse>()
        """
        ${data.hitokoto}
        --${data.fromWho ?: ""}「${data.from}」
        """.trimIndent()
    } else {
        "一言获取错误，请重试！"
    }
}

class HitokotoTimer : Job {
    override fun execute(context: JobExecutionContext?) {
        if (HitokotoConfig.enabled) {
            Bot.instances.forEach { bot ->
                if (bot.isOnline) {
                    HitokotoConfig.groups.forEach { group ->
                        runBlocking { bot.getGroup(group)?.sendMessage(getHitokoto()) }
                    }
                }
            }
        }
    }
}
