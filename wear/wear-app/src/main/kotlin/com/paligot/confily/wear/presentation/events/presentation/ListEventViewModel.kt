package com.paligot.confily.wear.presentation.events.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paligot.confily.core.events.EventRepository
import com.paligot.confily.models.ui.EventItemUi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ListEventUiState {
    data object Loading : ListEventUiState()
    data class Success(val modelUi: EventsModelUi) : ListEventUiState()
}

class ListEventViewModel(private val repository: EventRepository) : ViewModel() {
    val uiState = repository.events()
        .map { EventsModelUi(it.past.map { it.toModelUi() }.toImmutableList()) }
        .map { ListEventUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ListEventUiState.Loading
        )

    init {
        viewModelScope.launch {
            repository.fetchAndStoreEventList()
        }
    }

    fun onEventClick(id: String) = viewModelScope.launch {
        repository.saveEventId(id)
    }
}

private fun EventItemUi.toModelUi() = EventItemModelUi(id = id, name = name)
