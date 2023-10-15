package work.msdnicrosoft.mirai.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object TimeUtil {
    fun iso2LocalTime(time: String): String {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atOffset(ZoneOffset.UTC)
            .withOffsetSameInstant(ZoneOffset.ofHours(8))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    fun getFormattedDeltaTime(time: Long?): String {
        return if (time != null) {
            val delta = System.currentTimeMillis() - time

            val days = TimeUnit.MILLISECONDS.toDays(delta).let { if (it > 0) "$it 天 " else "" }
            val hours = (TimeUnit.MILLISECONDS.toHours(delta) % 24).toString().padStart(2, '0')
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(delta) % 60).toString().padStart(2, '0')
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(delta) % 60).toString().padStart(2, '0')

            "$days$hours:$minutes:$seconds"
        } else {
            "未知"
        }
    }
}
