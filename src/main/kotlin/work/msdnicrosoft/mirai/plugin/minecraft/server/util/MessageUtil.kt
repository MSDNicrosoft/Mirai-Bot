package work.msdnicrosoft.mirai.plugin.minecraft.server.util

import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.InstanceStatus

object MessageUtil {
    fun getStatusMessage(status: InstanceStatus): String {
        return when (status) {
            InstanceStatus.STOPPED -> "已停止"
            InstanceStatus.STOPPING -> "正在停止"
            InstanceStatus.STARTING -> "正在启动"
            InstanceStatus.RUNNING -> "正在运行"
            InstanceStatus.BUSY -> "状态未知"
        }
    }
}
