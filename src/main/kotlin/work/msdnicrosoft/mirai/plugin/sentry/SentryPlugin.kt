package work.msdnicrosoft.mirai.plugin.sentry

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.sentry.data.SentryIssue
import work.msdnicrosoft.mirai.util.NetworkUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SentryPlugin : CompositeCommand(
    MiraiBot,
    "sentry",
    description = "自动将错误日志上报至 Sentry 后获取查看地址"
) {

    @SubCommand("get")
    @Description("获取最新错误报告信息")
    suspend fun CommandSender.get() {
        val isConfigured = with(SentryConfig) {
            sentryUrl.isNotBlank() &&
                sentryToken.isNotBlank() &&
                organizationSlug.isNotBlank() &&
                projectSlug.isNotBlank()
        }
        if (isConfigured) {
            val client = NetworkUtil.client.config {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
            val resp = client.use {
                it.get {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = SentryConfig.sentryUrl
                        path("api/0/projects/${SentryConfig.organizationSlug}}/${SentryConfig.projectSlug}/issues/")
                        parameters {
                            append("query", "is:unresolved")
                            append("Authorization", "Bearer ${SentryConfig.sentryToken}")
                        }
                    }
                }
            }
            val message = if (resp.status.isSuccess()) {
                val latestIssue = resp.body<List<SentryIssue>?>()?.get(0)
                if (latestIssue != null) {
                    val formatTime = { time: String ->
                        val sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
                        val targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        LocalDateTime.parse(time, sourceFormatter).format(targetFormatter)
                    }
                    """
                    最新上报的错误如下：
                    标题：${latestIssue.title}
                    发生次数：${latestIssue.count}
                    大致发生位置：${latestIssue.culprit}
                    初次发生时间：${formatTime(latestIssue.firstSeen)}
                    最后发生时间：${formatTime(latestIssue.lastSeen)}
                    详情：${latestIssue.permalink}
                    """.trimIndent()
                } else {
                    "暂无报告，请重试！"
                }
            } else {
                "发生错误，请重试！"
            }
            sendMessage(message)
        } else {
            sendMessage("无法获取：未完全配置 Sentry")
        }
    }

    @SubCommand("on")
    @Description("启用 Sentry")
    suspend fun CommandSender.on() {
        SentryConfig.enabled = true
        sendMessage("已启用 Sentry，将在下次重启 Mirai 后生效")
    }

    @SubCommand("off")
    @Description("禁用 Sentry")
    suspend fun CommandSender.off() {
        SentryConfig.enabled = false
        sendMessage("已禁用 Sentry，将在下次重启 Mirai 后生效")
    }
}
