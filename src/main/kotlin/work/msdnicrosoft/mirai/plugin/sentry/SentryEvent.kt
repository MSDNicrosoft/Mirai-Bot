package work.msdnicrosoft.mirai.plugin.sentry

import io.sentry.Sentry

class SentryEvent : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        Sentry.captureException(e)
    }
}
