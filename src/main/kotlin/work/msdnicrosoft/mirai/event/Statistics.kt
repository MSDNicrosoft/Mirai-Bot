package work.msdnicrosoft.mirai.event

import net.mamoe.mirai.console.events.AutoLoginEvent
import net.mamoe.mirai.console.events.StartupEvent
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessagePreSendEvent
import work.msdnicrosoft.mirai.data.Statistics

object Statistics : ListenerHost {

    @EventHandler
    suspend fun MessageEvent.onReceive() {
        Statistics.receivedMessageCount += 1
    }

    @EventHandler
    suspend fun MessagePreSendEvent.onSend() {
        Statistics.sentMessageCount += 1
    }

    @EventHandler
    suspend fun AutoLoginEvent.onLogin() {
        Statistics.OnlineTime = System.currentTimeMillis()
    }

    @EventHandler
    suspend fun StartupEvent.onStartup() {
        Statistics.StartupTime = System.currentTimeMillis()
    }
}
