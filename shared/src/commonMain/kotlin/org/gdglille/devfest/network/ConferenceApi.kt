package org.gdglille.devfest.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.etag
import io.ktor.http.ifNoneMatch
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.gdglille.devfest.Platform
import org.gdglille.devfest.models.Attendee
import org.gdglille.devfest.models.Event
import org.gdglille.devfest.models.ScheduleItem

class ConferenceApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val eventId: String
) {
    suspend fun fetchEvent() = client.get("$baseUrl/events/$eventId").body<Event>()
    suspend fun fetchAttendee(barcode: String) =
        client.get("$baseUrl/events/$eventId/billet-web/$barcode")
            .body<Attendee>()

    suspend fun fetchAgenda(etag: String?): Pair<String, Map<String, Map<String, List<ScheduleItem>>>> {
        val response = client.get("$baseUrl/events/$eventId/agenda") {
            contentType(ContentType.parse("application/json"))
            accept(ContentType.parse("application/json; version=2"))
            etag?.let { ifNoneMatch(etag) }
        }
        return response.etag()!! to response.body()
    }

    companion object {
        fun create(baseUrl: String, eventId: String, enableNetworkLogs: Boolean): ConferenceApi =
            ConferenceApi(
                baseUrl = baseUrl,
                eventId = eventId,
                client = HttpClient(Platform().engine) {
                    install(
                        ContentNegotiation
                    ) {
                        json(
                            Json {
                                isLenient = true
                                ignoreUnknownKeys = true
                            }
                        )
                    }
                    if (enableNetworkLogs) {
                        install(
                            Logging
                        ) {
                            logger = Logger.DEFAULT
                            level = LogLevel.INFO
                        }
                    }
                }
            )
    }
}
