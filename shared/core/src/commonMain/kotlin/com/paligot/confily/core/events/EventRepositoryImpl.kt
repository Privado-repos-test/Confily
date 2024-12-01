package com.paligot.confily.core.events

import com.paligot.confily.core.QrCodeGenerator
import com.paligot.confily.core.api.ConferenceApi
import com.paligot.confily.core.api.exceptions.AgendaNotModifiedException
import com.paligot.confily.core.events.entities.CodeOfConduct
import com.paligot.confily.core.events.entities.Event
import com.paligot.confily.core.events.entities.EventItemList
import com.paligot.confily.core.events.entities.FeatureFlags
import com.paligot.confily.core.events.entities.MenuItem
import com.paligot.confily.core.events.entities.QAndAItem
import com.paligot.confily.core.kvalue.ConferenceSettings
import com.paligot.confily.core.kvalue.EventSavedException
import com.paligot.confily.core.networking.UserDao
import com.paligot.confily.core.networking.entities.UserTicket
import com.paligot.confily.core.partners.PartnerDao
import com.paligot.confily.core.schedules.SessionDao
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

@OptIn(ExperimentalCoroutinesApi::class)
internal class EventRepositoryImpl(
    private val api: ConferenceApi,
    private val settings: ConferenceSettings,
    private val eventDao: EventDao,
    private val sessionDao: SessionDao,
    private val userDao: UserDao,
    private val partnerDao: PartnerDao,
    private val qrCodeGenerator: QrCodeGenerator
) : EventRepository {
    override suspend fun fetchAndStoreEventList() = coroutineScope {
        val events = api.fetchEventList()
        eventDao.insertEventItems(future = events.future, past = events.past)
    }

    override suspend fun fetchAndStoreAgenda() {
        val eventId = settings.getEventId()
        val etag = settings.lastEtag(eventId)
        try {
            val (newEtag, agenda) = api.fetchAgenda(eventId, etag)
            sessionDao.insertAgenda(eventId, agenda)
            settings.updateEtag(eventId, newEtag)
        } catch (ex: AgendaNotModifiedException) {
            ex.printStackTrace()
        }
        val event = api.fetchEvent(eventId)
        val qanda = api.fetchQAndA(eventId)
        val partners = api.fetchPartnersActivities(eventId)
        eventDao.insertEvent(event, qanda)
        partnerDao.insertPartners(eventId, partners)
    }

    override fun events(): Flow<EventItemList> = eventDao.fetchEventList()

    override fun event(): Flow<Event?> = settings.fetchEventId()
        .flatMapConcat { eventDao.fetchEvent(eventId = it) }

    override fun ticket(): Flow<UserTicket?> = settings.fetchEventId()
        .flatMapConcat { userDao.fetchUserTicket(eventId = it) }

    override fun qanda(): Flow<ImmutableList<QAndAItem>> = settings.fetchEventId()
        .flatMapConcat { eventDao.fetchQAndA(eventId = it) }

    override fun menus(): Flow<ImmutableList<MenuItem>> = settings.fetchEventId()
        .flatMapConcat { eventDao.fetchMenus(eventId = it) }

    override fun coc(): Flow<CodeOfConduct> = settings.fetchEventId()
        .flatMapConcat { eventDao.fetchCoC(eventId = it) }

    override fun featureFlags(): Flow<FeatureFlags> = settings.fetchEventId()
        .flatMapConcat { eventDao.fetchFeatureFlags(eventId = it) }

    override fun isInitialized(defaultEvent: String?): Boolean = try {
        settings.getEventId()
        true
    } catch (_: EventSavedException) {
        if (defaultEvent != null) {
            settings.insertEventId(defaultEvent)
            true
        } else {
            false
        }
    }

    override fun saveEventId(eventId: String) {
        settings.insertEventId(eventId)
    }

    override fun deleteEventId() {
        settings.deleteEventId()
    }

    override suspend fun insertOrUpdateTicket(barcode: String) {
        val eventId = settings.getEventId()
        val attendee = try {
            val attendee = api.fetchAttendee(eventId, barcode)
            attendee
        } catch (ignored: Throwable) {
            null
        }
        val qrCode = qrCodeGenerator.generate(barcode)
        eventDao.updateTicket(eventId, qrCode, barcode, attendee)
    }
}
