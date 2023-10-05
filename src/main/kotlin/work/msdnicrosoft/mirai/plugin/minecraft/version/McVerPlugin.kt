package work.msdnicrosoft.mirai.plugin.minecraft.version

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.minecraft.version.data.MinecraftManifestResponse
import work.msdnicrosoft.mirai.util.NetworkUtil
import work.msdnicrosoft.mirai.util.iso2LocalTime

object McVerPlugin : CompositeCommand(
    MiraiBot,
    "mcver",
    description = "最新MC版本查询"
) {

    @SubCommand("je")
    @Description("查询最新 Java 版本的版本号及其发布时间")
    suspend fun CommandSender.je() {
        val client = NetworkUtil.client.config {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val resp = client.use {
            it.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "piston-meta.mojang.com"
                    path("mc/game/version_manifest_v2.json")
                }
            }
        }
        val message = if (resp.status.isSuccess()) {
            val data = resp.body<MinecraftManifestResponse>()

            val releaseVersion = data.latest.release
            val snapshotVersion = data.latest.snapshot

            val releaseTime = data.versions.firstOrNull { version ->
                version.versionName == releaseVersion
            }?.let { time -> iso2LocalTime(time.releaseTime) } ?: "暂无"
            val snapshotTime = data.versions.firstOrNull { version ->
                version.versionName == snapshotVersion
            }?.let { time -> iso2LocalTime(time.releaseTime) } ?: "暂无"
            """
            Minecraft: Java Edition
            最新正式版本：${data.latest.release}
            发布时间：$releaseTime
            ${"-".repeat(6)}
            最新快照版本：${data.latest.snapshot}
            发布时间：$snapshotTime
            注：发布时间为北京时间（UTC+08:00）
            """.trimIndent()
        } else {
            "查询失败：网络错误或解析错误！"
        }
        sendMessage(message)
    }
}
