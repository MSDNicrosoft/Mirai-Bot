package work.msdnicrosoft.mirai.plugin.blacklist

import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.MessageEvent
import work.msdnicrosoft.mirai.plugin.blacklist.data.BlacklistConfig

object BlacklistEvent : SimpleListenerHost() {

    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun MessageEvent.onBlacklistCheck() {
        if (!BlacklistConfig.enabled) return
        if ("${this.sender.id}" in BlacklistConfig.list && !this.isIntercepted) intercept()
    }
}
