package work.msdnicrosoft.mirai.plugin.minecraft.server

import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.minecraft.server.data.McServerControlConfig
import work.msdnicrosoft.mirai.plugin.minecraft.server.data.Response.InstanceStatusResponse
import work.msdnicrosoft.mirai.plugin.minecraft.server.data.Response.OutputResponse
import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.InstanceStatus
import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.OperationType
import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.enumFromInt
import work.msdnicrosoft.mirai.plugin.minecraft.server.util.MessageUtil
import work.msdnicrosoft.mirai.plugin.minecraft.server.util.ServerUtil

object McServerControlPlugin : CompositeCommand(
    MiraiBot,
    "server",
    description = "服务器管理（基于MCSM）"
) {

    @SubCommand("start")
    @Description("启动服务器")
    suspend fun CommandSender.start(server: String) {
        val status = ServerUtil.getInstanceStatus(server)
        val message = when (status) {
            InstanceStatus.STOPPED -> {
                runCatching {
                    ServerUtil.sendRequest(OperationType.START, server)
                }.onFailure {
                    sendMessage("网络请求出错：${it.message}")
                    return
                }
                delay(500)
                when (ServerUtil.getInstanceStatus(server)) {
                    InstanceStatus.STARTING -> "$server 正在启动"
                    InstanceStatus.RUNNING -> "$server 已启动"
                    else -> "$server 启动失败！"
                }
            }

            else -> "${MessageUtil.getStatusMessage(status)}，无法执行操作！"
        }
        sendMessage(message)
    }

    @SubCommand("stop")
    @Description("停止服务器")
    suspend fun CommandSender.stop(server: String) {
        val status = ServerUtil.getInstanceStatus(server)
        val message = when (status) {
            InstanceStatus.RUNNING -> {
                runCatching {
                    ServerUtil.sendRequest(OperationType.STOP, server)
                }.onFailure {
                    sendMessage("网络请求出错：${it.message}")
                    return
                }
                delay(500)
                when (ServerUtil.getInstanceStatus(server)) {
                    InstanceStatus.STOPPED -> "$server 已停止"
                    InstanceStatus.STOPPING -> "$server 正在停止"
                    else -> "$server 停止失败！"
                }
            }

            else -> "${MessageUtil.getStatusMessage(status)}，无法执行操作！"
        }
        sendMessage(message)
    }

    @SubCommand("kill")
    @Description("强制停止服务器")
    suspend fun CommandSender.kill(server: String) {
        val status = ServerUtil.getInstanceStatus(server)
        val message = when (status) {
            InstanceStatus.RUNNING,
            InstanceStatus.STARTING,
            InstanceStatus.STOPPING -> {
                runCatching {
                    ServerUtil.sendRequest(OperationType.KILL, server)
                }.onFailure {
                    sendMessage("网络请求出错：${it.message}")
                    return
                }
                delay(500)
                when (ServerUtil.getInstanceStatus(server)) {
                    InstanceStatus.STOPPED -> "已强制停止 $server"
                    InstanceStatus.STOPPING -> "正在强制停止 $server"
                    else -> "强制停止 $server 失败！"
                }
            }

            else -> "${MessageUtil.getStatusMessage(status)}，无法执行操作！"
        }
        sendMessage(message)
    }

    @SubCommand("restart")
    @Description("重启服务器")
    suspend fun CommandSender.restart(server: String) {
        val status = ServerUtil.getInstanceStatus(server)
        val message = when (status) {
            InstanceStatus.RUNNING -> {
                runCatching {
                    ServerUtil.sendRequest(OperationType.RESTART, server)
                }.onFailure {
                    sendMessage("网络请求出错：${it.message}")
                    return
                }
                delay(500)
                when (ServerUtil.getInstanceStatus(server)) {
                    InstanceStatus.STOPPED,
                    InstanceStatus.STOPPING,
                    InstanceStatus.STARTING,
                    InstanceStatus.RUNNING -> "$server 正在重启"

                    else -> "$server 重启失败！"
                }
            }

            else -> "${MessageUtil.getStatusMessage(status)}，无法执行操作！"
        }
        sendMessage(message)
    }

    @SubCommand("status")
    @Description("查看服务器状态")
    suspend fun CommandSender.status(server: String) {
        val resp = runCatching {
            ServerUtil.sendRequest(OperationType.STATUS, server)
        }.getOrElse {
            sendMessage("网络请求出错：${it.message}")
            return
        }

        if (resp.status.isSuccess()) {
            val data = resp.body<InstanceStatusResponse>().data
            val status = enumFromInt(data.status)
            val message = buildString {
                appendLine(MessageUtil.getStatusMessage(status))
                if (status == InstanceStatus.RUNNING) {
                    val info = data.info
                    if (info.version.isNotBlank()) {
                        appendLine(info.version)
                    }
                    if (info.maxPlayers != -1 && info.currentPlayers != -1) {
                        appendLine("当前在线：${info.currentPlayers}/${info.maxPlayers}")
                    }
                }
            }
            sendMessage(message)
        }
    }

    @SubCommand("list")
    @Description("查看可操作的服务器列表")
    suspend fun CommandSender.list() {
        sendMessage("当前可操作的服务器：${McServerControlConfig.instances.keys.joinToString()}")
    }

    @SubCommand("exec")
    @Description("执行指令")
    suspend fun CommandSender.exec(server: String, command: String) {
        val fullCommand = command.trim()

        val isNativeStopCommand = fullCommand.matches("""^(stop|end|restart)\b""".toRegex())

        // MCDR 命令：!!stop/restart  !!server stop/restart
        val isMCDRStopCommand = fullCommand.matches("""^!!(stop|restart)\b""".toRegex()) ||
            fullCommand.matches("""^!!server.*?(stop|restart)\b""".toRegex())

        if (isNativeStopCommand || isMCDRStopCommand) {
            sendMessage("请通过正确的方式执行此操作！")
            return
        }

        val status = ServerUtil.getInstanceStatus(server)
        val message = when (status) {
            InstanceStatus.RUNNING -> {
                val getOutput = {
                    runBlocking {
                        ServerUtil.sendRequest(OperationType.OUTPUT, server)
                            .body<OutputResponse>().data
                    }
                }
                runCatching {
                    val previousOutput = getOutput()

                    ServerUtil.sendRequest(OperationType.EXECUTE, server, mapOf("command" to fullCommand))
                    delay(2000)

                    val fullOutput = getOutput()
                    val output = fullOutput.replace(previousOutput, "")
                        .replace("\u001B", "")
                        .replace("[K", "")
                        .replace("\r", "")
                        .replace("\\[\\d+m".toRegex(), "")
                        .trim()
                    listOf(">$command", output).joinToString("\n")
                }.getOrElse {
                    "网络请求出错：${it.message}"
                }
            }

            else -> "${MessageUtil.getStatusMessage(status)}，无法执行操作！"
        }
        sendMessage(message)
    }
}
