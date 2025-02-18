package com.paligot.confily.backend.speakers

import com.paligot.confily.backend.receiveValidated
import com.paligot.confily.backend.speakers.SpeakerModule.speakerRepository
import com.paligot.confily.models.inputs.SpeakerInput
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.registerSpeakersRoutes() {
    val repository by speakerRepository

    get("/speakers") {
        val eventId = call.parameters["eventId"]!!
        call.respond(HttpStatusCode.OK, repository.list(eventId))
    }
    get("/speakers/{id}") {
        val eventId = call.parameters["eventId"]!!
        val speakerId = call.parameters["id"]!!
        call.respond(HttpStatusCode.OK, repository.get(eventId, speakerId))
    }
    post("/speakers") {
        val eventId = call.parameters["eventId"]!!
        val apiKey = call.request.headers["api_key"]!!
        val speaker = call.receiveValidated<SpeakerInput>()
        call.respond(HttpStatusCode.Created, repository.create(eventId, apiKey, speaker))
    }
    put("/speakers/{id}") {
        val eventId = call.parameters["eventId"]!!
        val apiKey = call.request.headers["api_key"]!!
        val speakerId = call.parameters["id"]!!
        val speaker = call.receiveValidated<SpeakerInput>()
        call.respond(HttpStatusCode.OK, repository.update(eventId, apiKey, speakerId, speaker))
    }
}
