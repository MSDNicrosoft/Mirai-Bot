package work.msdnicrosoft.mirai

import io.sentry.Sentry
import kotlinx.coroutines.cancel
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.PluginConfig
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.registerTo
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import work.msdnicrosoft.mirai.plugin.hitokoto.HitokotoTimer
import work.msdnicrosoft.mirai.plugin.hitokoto.data.HitokotoConfig
import work.msdnicrosoft.mirai.plugin.sentry.SentryEvent
import work.msdnicrosoft.mirai.plugin.sentry.data.SentryConfig
import java.time.LocalTime

object MiraiBot : KotlinPlugin(JvmPluginDescription.loadFromResource()) {

    private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()

    private val commands: List<Command> by services()
    private val configs: List<PluginConfig> by services()
    private val listeners: List<ListenerHost> by services()

    override fun onEnable() {
        for (config in configs) config.reload()
        for (command in commands) command.register(true)
        for (listener in listeners) (listener as SimpleListenerHost).registerTo(globalEventChannel())

        scheduler.start()
        registerScheduledTasks()

        if (SentryConfig.enabled) {
            initSentry()
        }

        logger.info("MSDNicrosoft-Mirai has been enabled!")
    }

    override fun onDisable() {
        scheduler.shutdown(false)

        for (command in commands) command.unregister()
        for (listener in listeners) (listener as SimpleListenerHost).cancel()

        saveAllPluginConfig()
    }

    private fun saveAllPluginConfig() {
        for (config in configs) config.save()
    }

    private fun registerScheduledTasks() {
        listOf(
            HitokotoTimer::class.java to LocalTime.of(HitokotoConfig.time.hour, HitokotoConfig.time.minute),
        ).forEach { (jobClass, targetTime) -> addScheduleJob(jobClass, targetTime) }
    }

    private fun addScheduleJob(jobClass: Class<out Job>, targetTime: LocalTime) {
        val job = JobBuilder.newJob(jobClass).build()
        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(targetTime.hour, targetTime.minute))
            .build()
        scheduler.scheduleJob(job, trigger)
    }

    private fun initSentry() {
        if (!MiraiBot.version.toString().endsWith("dev")) {
            Sentry.init {
                it.dsn = SentryConfig.sentryDsn
                it.release = MiraiBot.version.toString()
                it.shutdownTimeoutMillis = 2000
                it.isAttachStacktrace = true
                it.maxBreadcrumbs = 50
                it.environment = "Production"
            }
            Thread.setDefaultUncaughtExceptionHandler(SentryEvent())
        }
    }
}
