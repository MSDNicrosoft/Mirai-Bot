package work.msdnicrosoft.mirai

import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import work.msdnicrosoft.mirai.MiraiBot.reload
import work.msdnicrosoft.mirai.MiraiBot.save
import java.time.LocalTime

object MiraiBot : KotlinPlugin(JvmPluginDescription.loadFromResource()) {

    private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()

    override fun onEnable() {
        reloadAllPluginConfig()

        registerCommands()

        scheduler.start()
        registerScheduledTasks()

        logger.info("MSDNicrosoft-Mirai has been enabled!")
    }

    override fun onDisable() {
        scheduler.shutdown(false)
        saveAllPluginConfig()
    }

    private fun saveAllPluginConfig() {
    }

    private fun reloadAllPluginConfig() {
    }

    private fun registerCommands() {
        fun register(vararg commands: Command) {
            commands.forEach { command ->
                CommandManager.INSTANCE.registerCommand(command, true)
            }
        }
        // register()
    }

    private fun registerScheduledTasks() {
        listOf(
        ).forEach { (jobClass, targetTime) -> addScheduleJob(jobClass, targetTime) }
    }

    private fun addScheduleJob(jobClass: Class<out Job>, targetTime: LocalTime) {
        val job = JobBuilder.newJob(jobClass).build()
        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(targetTime.hour, targetTime.minute))
            .build()
        scheduler.scheduleJob(job, trigger)
    }
}
