package work.msdnicrosoft.mirai.plugin.blacklist

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import work.msdnicrosoft.mirai.MiraiBot
import work.msdnicrosoft.mirai.plugin.blacklist.data.BlacklistConfig

object BlacklistPlugin : CompositeCommand(
    MiraiBot,
    "bl",
    description = "全局黑名单管理"
) {
    @SubCommand("add")
    @Description("向黑名单中添加用户")
    suspend fun CommandSender.add(vararg userId: String) {
        val ignoredUser = mutableListOf<String>()
        val addedUser = mutableListOf<String>()
        userId.forEach {
            if (it.isNotEmpty()) {
                if (it in BlacklistConfig.list) {
                    ignoredUser.add(it)
                } else {
                    addedUser.add(it)
                }
            }
        }
        BlacklistConfig.list.addAll(addedUser)
        val message = buildString {
            if (!BlacklistConfig.enabled) appendLine("警告：黑名单处于关闭状态，若要使其生效，请开启黑名单")
            if (addedUser.isNotEmpty()) {
                appendLine("已向黑名单中添加 ${addedUser.size} 名用户，具体如下：")
                appendLine(addedUser.joinToString())
            }
            if (ignoredUser.isNotEmpty()) {
                appendLine("注意：其中 ${ignoredUser.size} 名用户在黑名单中已存在，具体如下：")
                appendLine(ignoredUser.joinToString())
            }
        }
        sendMessage(message)
    }

    @SubCommand("remove")
    @Description("从黑名单中移除用户")
    suspend fun CommandSender.remove(vararg userId: String) {
        val ignoredUser = mutableListOf<String>()
        val removedUser = mutableListOf<String>()
        BlacklistConfig.list.removeIf {
            val shouldRemove = it in userId
            if (shouldRemove) {
                removedUser.add(it)
            } else {
                ignoredUser.add(it)
            }
            shouldRemove
        }
        val message = buildString {
            if (!BlacklistConfig.enabled) appendLine("警告：黑名单处于关闭状态，若要使其生效，请开启黑名单")
            if (removedUser.isNotEmpty()) {
                appendLine("已向黑名单中移除 ${removedUser.size} 名用户，具体如下：")
                appendLine(removedUser.joinToString())
            }
            if (ignoredUser.isNotEmpty()) {
                appendLine("注意：其中 ${ignoredUser.size} 名用户在黑名单中不存在，具体如下：")
                appendLine(ignoredUser.joinToString())
            }
        }
        sendMessage(message)
    }

    @SubCommand("query")
    @Description("查询用户是否在黑名单内")
    suspend fun CommandSender.query(vararg userId: String) {
        val foundUser = mutableListOf<String>()
        val notFoundUser = mutableListOf<String>()
        userId.forEach {
            if (it.isNotEmpty()) {
                if (it in BlacklistConfig.list) {
                    foundUser.add(it)
                } else {
                    notFoundUser.add(it)
                }
            }
        }
        val message = buildString {
            if (foundUser.isNotEmpty()) {
                appendLine("以下用户存在于黑名单中：")
                appendLine(foundUser.joinToString())
            }
            if (notFoundUser.isNotEmpty()) {
                appendLine("以下用户在黑名单中不存在：")
                appendLine(notFoundUser.joinToString())
            }
        }
        sendMessage(message)
    }

    @SubCommand("list")
    @Description("列出黑名单内所有用户")
    suspend fun CommandSender.list() {
        val message = if (BlacklistConfig.list.isEmpty()) {
            "当前黑名单为空！"
        } else {
            """
            当前黑名单内有 ${BlacklistConfig.list.size} 名用户，具体如下：
            ${BlacklistConfig.list.joinToString()}
            """.trimIndent()
        }
        sendMessage(message)
    }

    @SubCommand("on")
    @Description("启用黑名单")
    suspend fun CommandSender.on() {
        BlacklistConfig.enabled = true
        sendMessage("已启用黑名单")
    }

    @SubCommand("off")
    @Description("禁用黑名单")
    suspend fun CommandSender.off() {
        BlacklistConfig.enabled = false
        sendMessage("已禁用黑名单")
    }
}
