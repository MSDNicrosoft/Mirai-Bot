package work.msdnicrosoft.mirai

import io.sentry.Sentry
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import work.msdnicrosoft.mirai.plugin.crazythursday.CrazyThursdayPlugin
import work.msdnicrosoft.mirai.plugin.hitokoto.HitokotoConfig
import work.msdnicrosoft.mirai.plugin.hitokoto.HitokotoPlugin
import work.msdnicrosoft.mirai.plugin.hitokoto.HitokotoTimer
import work.msdnicrosoft.mirai.plugin.minecraft.version.McVerPlugin
import work.msdnicrosoft.mirai.plugin.sentry.SentryConfig
import work.msdnicrosoft.mirai.plugin.sentry.SentryEvent
import work.msdnicrosoft.mirai.plugin.sentry.SentryPlugin
import java.time.LocalTime

object MiraiBot : KotlinPlugin(JvmPluginDescription.loadFromResource()) {

    private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()

    override fun onEnable() {
        reloadAllPluginConfig()

        registerCommands()

        scheduler.start()
        registerScheduledTasks()

        if (SentryConfig.enabled) {
            initSentry()
        }

        logger.info("MSDNicrosoft-Mirai has been enabled!")
    }

    override fun onDisable() {
        scheduler.shutdown(false)
        saveAllPluginConfig()
    }

    private fun saveAllPluginConfig() {
        HitokotoConfig.save()
        SentryConfig.save()
    }

    private fun reloadAllPluginConfig() {
        HitokotoConfig.reload()
        SentryConfig.reload()
    }

    private fun registerCommands() {
        fun register(vararg commands: Command) {
            commands.forEach { command ->
                CommandManager.INSTANCE.registerCommand(command, true)
            }
        }
        // register()
            HitokotoPlugin,
            CrazyThursdayPlugin,
            McVerPlugin,
            SentryPlugin
        )
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
