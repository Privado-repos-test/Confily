package com.paligot.confily.core.events

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.paligot.confily.core.agenda.convertToModelDb
import com.paligot.confily.core.events.entities.CodeOfConduct
import com.paligot.confily.core.events.entities.EventInfo
import com.paligot.confily.core.events.entities.EventItemList
import com.paligot.confily.core.events.entities.FeatureFlags
import com.paligot.confily.core.events.entities.MenuItem
import com.paligot.confily.core.events.entities.QAndAAction
import com.paligot.confily.core.events.entities.QAndAItem
import com.paligot.confily.db.ConfilyDatabase
import com.paligot.confily.models.Attendee
import com.paligot.confily.models.EventV4
import com.paligot.confily.models.QuestionAndResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext
import com.paligot.confily.models.EventItemList as EventItemListNetworking

class EventDaoSQLDelight(
    private val db: ConfilyDatabase,
    private val dispatcher: CoroutineContext
) : EventDao {
    override fun fetchEventList(): Flow<EventItemList> = combine(
        db.eventQueries.selectEventItem(true, eventItemMapper).asFlow().mapToList(dispatcher),
        db.eventQueries.selectEventItem(false, eventItemMapper).asFlow().mapToList(dispatcher),
        transform = { past, future -> EventItemList(future = future, past = past) }
    )

    override fun fetchEvent(eventId: String): Flow<EventInfo?> =
        db.eventQueries.selectEvent(eventId, eventInfoMapper)
            .asFlow()
            .mapToOneOrNull(dispatcher)

    override fun fetchQAndA(eventId: String): Flow<ImmutableList<QAndAItem>> =
        db.transactionWithResult {
            return@transactionWithResult combine(
                db.qAndAQueries.selectQAndA(eventId).asFlow().mapToList(dispatcher),
                db.qAndAQueries.selectQAndAActions(eventId).asFlow().mapToList(dispatcher),
                transform = { qAndADb, actionsDb ->
                    qAndADb.map { qanda ->
                        QAndAItem(
                            question = qanda.question,
                            answer = qanda.response,
                            actions = actionsDb
                                .filter { it.qanda_id == qanda.order_ }
                                .sortedBy { it.order_ }
                                .map { QAndAAction(label = it.label, url = it.url) }
                        )
                    }.toImmutableList()
                }
            )
        }

    override fun fetchMenus(eventId: String): Flow<ImmutableList<MenuItem>> =
        db.menuQueries.selectMenus(eventId, menuMapper)
            .asFlow()
            .mapToList(dispatcher)
            .map { it.toImmutableList() }

    override fun fetchCoC(eventId: String): Flow<CodeOfConduct> =
        db.eventQueries.selectCoc(eventId, cocMapper).asFlow().mapToOne(dispatcher)

    override fun fetchFeatureFlags(eventId: String): Flow<FeatureFlags> = db
        .featuresActivatedQueries
        .selectFeatures(eventId)
        .asFlow()
        .mapToOneOrNull(dispatcher)
        .map { features ->
            FeatureFlags(
                hasNetworking = features?.has_networking ?: false,
                hasSpeakerList = features?.has_speaker_list ?: false,
                hasPartnerList = features?.has_partner_list ?: false,
                hasMenus = features?.has_menus ?: false,
                hasQAndA = features?.has_qanda ?: false,
                hasTicketIntegration = features?.has_billet_web_ticket ?: false
            )
        }

    override fun insertEvent(event: EventV4, qAndA: List<QuestionAndResponse>) = db.transaction {
        val eventDb = event.convertToModelDb()
        db.eventQueries.insertEvent(
            id = eventDb.id,
            name = eventDb.name,
            formatted_address = eventDb.formatted_address,
            address = eventDb.address,
            latitude = eventDb.latitude,
            longitude = eventDb.longitude,
            date = eventDb.date,
            start_date = eventDb.start_date,
            end_date = eventDb.end_date,
            coc = eventDb.coc,
            openfeedback_project_id = eventDb.openfeedback_project_id,
            contact_email = eventDb.contact_email,
            contact_phone = eventDb.contact_phone,
            faq_url = eventDb.faq_url,
            coc_url = eventDb.coc_url,
            updated_at = eventDb.updated_at
        )
        event.socials.forEach {
            db.socialQueries.insertSocial(
                url = it.url,
                type = it.type.name.lowercase(),
                ext_id = eventDb.id,
                event_id = eventDb.id
            )
        }
        qAndA.forEach { qAndA ->
            db.qAndAQueries.insertQAndA(
                qAndA.order.toLong(),
                eventDb.id,
                qAndA.question,
                qAndA.response
            )
            qAndA.actions.forEach {
                db.qAndAQueries.insertQAndAAction(
                    id = "${qAndA.id}-${it.order}",
                    order_ = it.order.toLong(),
                    event_id = eventDb.id,
                    qanda_id = qAndA.order.toLong(),
                    label = it.label,
                    url = it.url
                )
            }
        }
        event.menus.forEach {
            db.menuQueries.insertMenu(it.name, it.dish, it.accompaniment, it.dessert, event.id)
        }
        db.featuresActivatedQueries.insertFeatures(
            event_id = eventDb.id,
            has_networking = event.features.hasNetworking,
            has_speaker_list = event.features.hasSpeakerList,
            has_partner_list = event.features.hasPartnerList,
            has_menus = event.features.hasMenus,
            has_qanda = event.features.hasQAndA,
            has_billet_web_ticket = event.features.hasBilletWebTicket
        )
    }

    override fun insertEventItems(
        future: List<EventItemListNetworking>,
        past: List<EventItemListNetworking>
    ) = db.transaction {
        future.forEach {
            val itemDb = it.convertToModelDb(false)
            db.eventQueries.insertEventItem(
                id = itemDb.id,
                name = itemDb.name,
                date = itemDb.date,
                timestamp = itemDb.timestamp,
                past = itemDb.past
            )
        }
        past.forEach {
            val itemDb = it.convertToModelDb(true)
            db.eventQueries.insertEventItem(
                id = itemDb.id,
                name = itemDb.name,
                date = itemDb.date,
                timestamp = itemDb.timestamp,
                past = itemDb.past
            )
        }
    }

    override fun updateTicket(
        eventId: String,
        qrCode: ByteArray,
        barcode: String,
        attendee: Attendee?
    ) = db.ticketQueries.insertTicket(
        id = attendee?.id,
        ext_id = attendee?.idExt,
        event_id = eventId,
        email = attendee?.email,
        firstname = attendee?.firstname,
        lastname = attendee?.name,
        barcode = barcode,
        qrcode = qrCode
    )
}
