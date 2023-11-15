package org.gdglille.devfest.android.theme.m3.infos.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.gdglille.devfest.android.theme.m3.navigation.TabActions
import org.gdglille.devfest.android.theme.m3.navigation.TopActions
import org.gdglille.devfest.android.theme.m3.style.actions.TabAction
import org.gdglille.devfest.android.theme.m3.style.actions.TabActionsUi
import org.gdglille.devfest.android.theme.m3.style.actions.TopActionsUi
import org.gdglille.devfest.repositories.AgendaRepository
import org.gdglille.devfest.repositories.EventRepository

sealed class InfoUiState {
    data object Loading : InfoUiState()
    data class Success(val topActionsUi: TopActionsUi, val tabActionsUi: TabActionsUi) :
        InfoUiState()

    data class Failure(val throwable: Throwable) : InfoUiState()
}

class InfoViewModel(
    private val agendaRepository: AgendaRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    val uiState = agendaRepository.scaffoldConfig()
        .map { config ->
            InfoUiState.Success(
                topActionsUi = TopActionsUi(
                    actions = persistentListOf(TopActions.disconnect),
                    maxActions = 0
                ),
                tabActionsUi = TabActionsUi(
                    scrollable = true,
                    actions = arrayListOf<TabAction>().apply {
                        add(TabActions.event)
                        if (config.hasMenus) {
                            add(TabActions.menus)
                        }
                        if (config.hasQAndA) {
                            add(TabActions.qanda)
                        }
                        add(TabActions.coc)
                    }.toImmutableList()
                )
            )
        }
        .catch {
            Firebase.crashlytics.recordException(it)
            InfoUiState.Failure(it)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = InfoUiState.Loading,
            started = SharingStarted.WhileSubscribed()
        )

    fun disconnect() = viewModelScope.launch {
        eventRepository.deleteEventId()
    }

    object Factory {
        fun create(agendaRepository: AgendaRepository, eventRepository: EventRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    InfoViewModel(
                        agendaRepository = agendaRepository,
                        eventRepository = eventRepository
                    ) as T
            }
    }
}
