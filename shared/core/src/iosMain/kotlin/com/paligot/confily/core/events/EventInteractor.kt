package com.paligot.confily.core.events

import com.paligot.confily.core.events.entities.TeamMember
import com.paligot.confily.core.events.entities.TeamMemberItem
import com.paligot.confily.core.events.entities.mapToEventInfoUi
import com.paligot.confily.core.events.entities.mapToEventItemListUi
import com.paligot.confily.core.events.entities.mapToMenuItemUi
import com.paligot.confily.core.events.entities.mapToTeamMemberItemUi
import com.paligot.confily.core.events.entities.mapToTeamMemberUi
import com.paligot.confily.core.networking.entities.mapToTicketUi
import com.paligot.confily.models.ui.EventItemListUi
import com.paligot.confily.models.ui.EventUi
import com.paligot.confily.models.ui.MenuItemUi
import com.paligot.confily.models.ui.TeamMemberItemUi
import com.paligot.confily.models.ui.TeamMemberUi
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class EventInteractor(
    private val repository: EventRepository
) {
    @NativeCoroutines
    suspend fun fetchAndStoreEventList() = repository.fetchAndStoreEventList()

    @NativeCoroutines
    suspend fun fetchAndStoreAgenda() = repository.fetchAndStoreAgenda()

    @NativeCoroutines
    fun events(): Flow<EventItemListUi> = repository.events()
        .map { eventItemList -> eventItemList.mapToEventItemListUi() }

    @NativeCoroutines
    fun event(): Flow<EventUi?> = combine(
        flow = repository.event(),
        flow2 = repository.ticket(),
        transform = { event, ticket ->
            if (event == null) return@combine null
            EventUi(
                eventInfo = event.mapToEventInfoUi(),
                ticket = ticket?.mapToTicketUi()
            )
        }
    )

    @NativeCoroutines
    fun menus(): Flow<ImmutableList<MenuItemUi>> = repository.menus()
        .map { menus -> menus.map { it.mapToMenuItemUi() }.toImmutableList() }

    @NativeCoroutines
    suspend fun insertOrUpdateTicket(barcode: String) = repository.insertOrUpdateTicket(barcode)

    @NativeCoroutines
    fun teamMembers(): Flow<List<TeamMemberItemUi>> = repository.teamMembers()
        .map { it.map { it.mapToTeamMemberItemUi() } }

    @NativeCoroutines
    fun teamMember(memberId: String): Flow<TeamMemberUi?> = repository.teamMember(memberId)
        .map { it?.mapToTeamMemberUi() }
}
