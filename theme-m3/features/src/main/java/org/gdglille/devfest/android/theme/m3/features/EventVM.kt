package org.gdglille.devfest.android.theme.m3.features

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import org.gdglille.devfest.android.data.viewmodels.EventUiState
import org.gdglille.devfest.android.data.viewmodels.EventViewModel
import org.gdglille.devfest.android.screens.event.Event
import org.gdglille.devfest.repositories.AgendaRepository

@Composable
fun EventVM(
    agendaRepository: AgendaRepository,
    modifier: Modifier = Modifier,
    onLinkClicked: (url: String?) -> Unit,
    onItineraryClicked: (lat: Double, lng: Double) -> Unit
) {
    val viewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory.create(agendaRepository)
    )
    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        is EventUiState.Loading -> Event(
            event = (uiState.value as EventUiState.Loading).event,
            modifier = modifier,
            isLoading = true,
            onLinkClicked = onLinkClicked,
            onItineraryClicked = { _, _ -> }
        )

        is EventUiState.Failure -> Text(text = stringResource(id = R.string.text_error))
        is EventUiState.Success -> Event(
            event = (uiState.value as EventUiState.Success).event,
            modifier = modifier,
            onLinkClicked = onLinkClicked,
            onItineraryClicked = onItineraryClicked
        )
    }
}
