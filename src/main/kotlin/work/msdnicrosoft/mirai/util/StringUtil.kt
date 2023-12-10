package work.msdnicrosoft.mirai.util

object StringUtil {
    /**
     * Converts a string to a boolean value.
     *
     * @param string The input string to be converted.
     * @return `true` if the input string is equal to "TRUE" or "Y" (case-insensitive), `false` otherwise.
     */
    fun string2Boolean(string: String): Boolean {
        return when (string.uppercase()) {
            "TRUE", "Y" -> true
            else -> false
        }
    }
}
