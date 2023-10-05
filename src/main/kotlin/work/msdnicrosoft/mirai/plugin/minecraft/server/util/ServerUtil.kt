package work.msdnicrosoft.mirai.plugin.minecraft.server.util

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import work.msdnicrosoft.mirai.plugin.minecraft.server.McServerControlConfig
import work.msdnicrosoft.mirai.plugin.minecraft.server.data.InstanceStatusResponse
import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.InstanceStatus
import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.OperationType
import work.msdnicrosoft.mirai.plugin.minecraft.server.enum.enumFromInt
import work.msdnicrosoft.mirai.util.NetworkUtil

object ServerUtil {
    suspend fun getInstanceStatus(name: String): InstanceStatus {
        return runCatching {
            enumFromInt(sendRequest(OperationType.STATUS, name).body<InstanceStatusResponse>().data.status)
        }.getOrElse { InstanceStatus.BUSY }
    }

    suspend fun sendRequest(
        operationType: OperationType,
        server: String,
        extraParameters: Map<String, String> = mapOf()
    ): HttpResponse {
        val path = when (operationType) {
            OperationType.STATUS -> "api/instance"
            OperationType.START -> "api/protected_instance/open"
            OperationType.STOP -> "api/protected_instance/stop"
            OperationType.RESTART -> "api/protected_instance/restart"
            OperationType.KILL -> "api/protected_instance/kill"
            OperationType.EXECUTE -> "api/protected_instance/command"
            OperationType.OUTPUT -> "api/protected_instance/outputlog"
        }
        val client = NetworkUtil.client.config {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            headers {
                append("Content-Type", "application/json; charset=utf-8")
                append("X-Requested-With", "XMLHttpRequest")
            }
        }
        client.use {
            return it.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = McServerControlConfig.request.host
                    path(path)
                    parameters {
                        append("apikey", McServerControlConfig.request.apiKey)
                        append("uuid", McServerControlConfig.instances[server]!!)
                        append("remote_uuid", McServerControlConfig.request.daemonUuid)
                        extraParameters.forEach { param ->
                            append(param.key, param.value)
                        }
                    }
                }
            }
        }
    }
}
