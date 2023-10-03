package work.msdnicrosoft.mirai.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun iso2LocalTime(time: String): String {
    return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .atOffset(ZoneOffset.UTC)
        .withOffsetSameInstant(ZoneOffset.ofHours(8))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}
