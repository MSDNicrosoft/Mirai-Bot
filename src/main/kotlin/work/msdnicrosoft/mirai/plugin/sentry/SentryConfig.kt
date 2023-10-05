package work.msdnicrosoft.mirai.plugin.sentry

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object SentryConfig : AutoSavePluginConfig("sentry") {
    @ValueDescription("是否启用 Sentry")
    var enabled by value<Boolean>(false)

    @ValueDescription("Sentry URL")
    val sentryUrl by value<String>("")

    @ValueDescription("Sentry DSN")
    val sentryDsn by value<String>("")

    @ValueDescription("Sentry Token")
    val sentryToken by value<String>("")

    @ValueDescription("Organization Slug")
    val organizationSlug by value<String>("")

    @ValueDescription("Project Slug")
    val projectSlug by value<String>("")
}
