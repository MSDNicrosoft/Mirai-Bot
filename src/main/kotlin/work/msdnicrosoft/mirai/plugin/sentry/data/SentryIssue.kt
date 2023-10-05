package work.msdnicrosoft.mirai.plugin.sentry.data

import kotlinx.serialization.Serializable

@Serializable
data class SentryIssue(
    val firstSeen: String,
    val lastSeen: String,
    val title: String,
    val count: String,
    val culprit: String,
    val permalink: String
)
