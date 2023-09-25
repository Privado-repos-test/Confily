package org.gdglille.devfest.backend.jobs

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.gdglille.devfest.backend.events.EventDao
import org.gdglille.devfest.backend.internals.network.welovedevs.WeLoveDevsApi
import org.gdglille.devfest.backend.partners.PartnerDao

fun Route.registerJobRoutes(
    wldApi: WeLoveDevsApi,
    eventDao: EventDao,
    partnerDao: PartnerDao,
    jobDao: JobDao
) {
    val repository = JobRepository(wldApi, eventDao, partnerDao, jobDao)

    post("jobs/wld/import") {
        val apiKey = call.request.headers["api_key"]!!
        val eventId = call.parameters["eventId"]!!
        call.respond(HttpStatusCode.Created, repository.importWld(eventId, apiKey))
    }
}
