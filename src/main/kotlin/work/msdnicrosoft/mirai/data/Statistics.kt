package work.msdnicrosoft.mirai.data

import work.msdnicrosoft.mirai.util.TimeUtil

object Statistics {
    var sentMessageCount: Int = 0
        set(value) {
            field = if (value >= 0) value else field
        }

    var receivedMessageCount: Int = 0
        set(value) {
            field = if (value >= 0) value else field
        }

    var OnlineTime: Long? = null

    var StartupTime: Long? = null

    fun getOnlineTime(): String {
        return TimeUtil.getFormattedDeltaTime(OnlineTime)
    }

    fun getStartupTime(): String {
        return TimeUtil.getFormattedDeltaTime(StartupTime)
    }
}
