package work.msdnicrosoft.mirai.plugin.minecraft.uuid

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.minecraft.uuid.data.Response.ProfileResponse
import work.msdnicrosoft.mirai.plugin.minecraft.uuid.data.Response.UuidResponse
import work.msdnicrosoft.mirai.util.NetworkUtil

object McUuidPlugin : CompositeCommand(
    MiraiBot,
    "mcuuid",
    description = "互查用户名 和 MC UUID"
) {
    @SubCommand("uuid")
    @Description("通过当前用户名查询 UUID")
    suspend fun CommandContext.uuid(name: String) {
        val client = NetworkUtil.client.config {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val resp = client.use {
            it.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.mojang.com"
                    path("users/profiles/minecraft/$name")
                }
            }
        }
        val message = if (resp.status.isSuccess()) {
            val data = resp.body<UuidResponse>()
            """
            当前用户名为 $name 的 UUID 为：
            ${data.id}
            """.trimIndent()
        } else {
            "无效的用户名或请求失败"
        }
        sender.sendMessage(message)
    }

    @SubCommand("name")
    @Description("通过 UUID 查询当前用户名")
    suspend fun CommandContext.name(uuid: String) {
        val client = NetworkUtil.client.config {
            install(ContentNegotiation) { json() }
        }
        val resp = client.use {
            it.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "sessionserver.mojang.com"
                    path("session/minecraft/profile/${uuid.replace("-", "")}")
                }
            }
        }
        val message = if (resp.status.isSuccess()) {
            val data = resp.body<ProfileResponse>()
            """
            UUID 为 $uuid 的当前用户名为：
            ${data.id}
            """.trimIndent()
        } else {
            "无效的 UUID 或请求失败"
        }
        sender.sendMessage(message)
    }
}
