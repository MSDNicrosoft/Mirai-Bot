package work.msdnicrosoft.mirai.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*

object NetworkUtil {
    val client: HttpClient by lazy {
        HttpClient(CIO) {
            headers {
                append(
                    "User-Agent",
                    mutableListOf(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
                        "AppleWebKit/537.36 (KHTML, like Gecko)",
                        "Chrome/115.0.0.0",
                        "Safari/537.36",
                        "Edg/115.0.1901.183"
                    ).joinToString(" ")
                )
            }
        }
    }
}
