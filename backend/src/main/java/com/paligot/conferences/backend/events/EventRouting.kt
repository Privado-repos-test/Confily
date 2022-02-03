package com.paligot.conferences.backend.events

import com.paligot.conferences.backend.NotFoundException
import com.paligot.conferences.backend.partners.PartnerDao
import com.paligot.conferences.backend.partners.Sponsorship
import com.paligot.conferences.backend.receiveValidated
import com.paligot.conferences.backend.schedulers.ScheduleItemDao
import com.paligot.conferences.backend.schedulers.convertToModel
import com.paligot.conferences.backend.speakers.SpeakerDao
import com.paligot.conferences.backend.talks.TalkDao
import com.paligot.conferences.backend.talks.convertToModel
import com.paligot.conferences.models.Agenda
import com.paligot.conferences.models.inputs.EventInput
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

fun Route.registerEventRoutes(
    eventDao: EventDao,
    speakerDao: SpeakerDao,
    talkDao: TalkDao,
    scheduleItemDao: ScheduleItemDao,
    partnerDao: PartnerDao
) {
    get {
        val eventId = call.parameters["eventId"]!!
        val event = eventDao.get(eventId) ?: throw NotFoundException("Event $eventId Not Found")
        val year = event.startDate.split("-")[0]
        call.respond(HttpStatusCode.OK, event.convertToModel(
            golds = partnerDao.listValidated(year, Sponsorship.Gold),
            silvers = partnerDao.listValidated(year, Sponsorship.Silver),
            bronzes = partnerDao.listValidated(year, Sponsorship.Bronze),
            others = emptyList()
        ))
    }
    put {
        val eventId = call.parameters["eventId"]!!
        val input = call.receiveValidated<EventInput>()
        eventDao.createOrUpdate(input.convertToDb(eventId))
        call.respond(HttpStatusCode.OK, eventId)
    }
    get("agenda") {
        val eventId = call.parameters["eventId"]!!
        eventDao.get(eventId)  ?: throw NotFoundException("Event $eventId Not Found")
        val schedules = scheduleItemDao.getAll(eventId).groupBy { it.time }.entries.map {
            async {
                val scheduleItems = it.value.map {
                    async {
                        if (it.talkId == null) it.convertToModel(null)
                        else {
                            val talk = talkDao.get(eventId, it.talkId) ?: return@async it.convertToModel(null)
                            it.convertToModel(
                                talk.convertToModel(speakerDao.getByIds(eventId, *talk.speakerIds.toTypedArray()))
                            )
                        }
                    }
                }.awaitAll()
                return@async it.key to scheduleItems
            }
        }.awaitAll().associate { it }.toSortedMap()
        call.respond(HttpStatusCode.OK, Agenda(talks = schedules))
    }
}