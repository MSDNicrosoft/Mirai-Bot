package work.msdnicrosoft.mirai.plugin.todayinhistory

import com.google.gson.JsonParser
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.util.NetworkUtil
import java.time.LocalDate
import kotlin.math.min

object TodayInHistoryPlugin : SimpleCommand(
    MiraiBot,
    "history",
    description = "查看历史上的今天"
) {
    @Handler
    suspend fun CommandSender.onCommand() {
        val now = LocalDate.now()
        val month = now.monthValue.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')

        val client = NetworkUtil.client.config { install(ContentNegotiation) }
        val resp = client.use {
            it.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "baike.baidu.com"
                    path("cms/home/eventsOnHistory/$month.json")
                }
            }
        }

        val message = if (resp.status.isSuccess()) {
            val content = JsonParser.parseString(resp.body<String>())
                .asJsonObject[month]
                .asJsonObject["$month$day"]
                .asJsonArray
            mutableListOf<String>().apply {
                val festival = content[0].asJsonObject["festival"].asString
                if (festival.isNotBlank()) {
                    add("今天是$festival")
                    add("-".repeat(5))
                }
                add("# 下面是历史上的今天：")
                for (event in content.take(min(7, content.size()))) {
                    val year = event.asJsonObject["year"].asString
                    val title = event.asJsonObject["title"].asString
                        .replace("<([\\s\\S])*?>".toRegex(), "")
                        .replace("\n", "")
                    add("$year：$title")
                }
            }.joinToString("\n")
        } else {
            "发生错误，请重试或反馈：${resp.status.description}"
        }
        sendMessage(message)
    }
}
