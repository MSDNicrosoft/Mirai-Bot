package work.msdnicrosoft.mirai.plugin.minecraft.server.enum

enum class InstanceStatus(val value: Int) {
    STOPPED(0),
    STOPPING(1),
    STARTING(2),
    RUNNING(3),
    BUSY(-1)
}

fun enumFromInt(value: Int): InstanceStatus {
    return enumValues<InstanceStatus>().find { it.value == value } ?: InstanceStatus.BUSY
}
