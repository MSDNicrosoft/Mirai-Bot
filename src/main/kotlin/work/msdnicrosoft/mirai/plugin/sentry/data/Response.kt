package work.msdnicrosoft.mirai.plugin.sentry.data

import kotlinx.serialization.Serializable

object Response {
    @Serializable
    data class SentryIssueResponse(
        val firstSeen: String,
        val lastSeen: String,
        val title: String,
        val count: String,
        val culprit: String,
        val permalink: String
    )
}
