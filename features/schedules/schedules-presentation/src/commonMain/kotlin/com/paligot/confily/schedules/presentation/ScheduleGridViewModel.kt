package com.paligot.confily.schedules.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.lyricist.Lyricist
import com.paligot.confily.core.AlarmScheduler
import com.paligot.confily.core.schedules.SessionRepository
import com.paligot.confily.core.schedules.entities.Sessions
import com.paligot.confily.core.schedules.entities.mapToListUi
import com.paligot.confily.navigation.TopActions
import com.paligot.confily.resources.Strings
import com.paligot.confily.schedules.panes.models.AgendaUi
import com.paligot.confily.schedules.ui.models.TalkItemUi
import com.paligot.confily.style.theme.actions.TabAction
import com.paligot.confily.style.theme.actions.TabActionsUi
import com.paligot.confily.style.theme.actions.TopActionsUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.StringResource

data class ScheduleUi(
    val topActionsUi: TopActionsUi,
    val tabActionsUi: TabActionsUi,
    val tabIndexSelected: Int?,
    val schedules: ImmutableList<AgendaUi>
)

sealed class ScheduleGridUiState {
    data class Loading(val agenda: ImmutableList<AgendaUi>) : ScheduleGridUiState()
    data class Success(val scheduleUi: ScheduleUi) : ScheduleGridUiState()
    data class Failure(val throwable: Throwable) : ScheduleGridUiState()
}

@FlowPreview
@ExperimentalCoroutinesApi
class ScheduleGridViewModel(
    sessionRepository: SessionRepository,
    lyricist: Lyricist<Strings>,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val _countFilters = sessionRepository.countFilters()
    private val _sessions = sessionRepository.sessions()
    val uiState: StateFlow<ScheduleGridUiState> =
        combine(
            flow = _countFilters,
            flow2 = _sessions,
            transform = { countFilters, sessions ->
                if (sessions.sessions.isNotEmpty()) {
                    val scheduleUi = sessions.mapToUiModel(countFilters, lyricist.strings)
                    ScheduleGridUiState.Success(
                        scheduleUi
                    )
                } else {
                    ScheduleGridUiState.Loading(persistentListOf(AgendaUi.fake))
                }
            }
        ).catch {
            it.printStackTrace()
            emit(ScheduleGridUiState.Failure(it))
        }.stateIn(
            scope = viewModelScope,
            initialValue = ScheduleGridUiState.Loading(persistentListOf(AgendaUi.fake)),
            started = SharingStarted.WhileSubscribed()
        )

    fun markAsFavorite(talkItem: TalkItemUi) = viewModelScope.launch {
        alarmScheduler.schedule(talkItem)
    }
}

private fun Sessions.mapToUiModel(countFilters: Int, strings: Strings): ScheduleUi = ScheduleUi(
    topActionsUi = countFilters.mapToTopActions(),
    tabActionsUi = mapToTabActions(),
    tabIndexSelected = tabSelected(),
    schedules = mapToListUi(strings)
)

private fun Int.mapToTopActions(): TopActionsUi = TopActionsUi(
    actions = persistentListOf(if (this > 0) TopActions.filtersFilled else TopActions.filters)
)

@OptIn(InternalResourceApi::class)
private fun Sessions.mapToTabActions(): TabActionsUi = TabActionsUi(
    scrollable = true,
    actions = this.sessions.keys
        .sorted()
        .map {
            val label = it.format(LocalDate.Format { byUnicodePattern("dd/MM") })
            TabAction(
                route = label,
                labelId = StringResource("", "", emptySet()),
                label = label
            )
        }
        .toImmutableList()
)

private fun Sessions.tabSelected(): Int? {
    val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    val daysSorted = this.sessions.keys.sorted()
    val tabSelected = daysSorted.indexOf(element = now.date)
    return if (tabSelected == -1) null else tabSelected
}
