package org.gdglille.devfest.android.theme.m3.schedules.feature

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.gdglille.devfest.AlarmScheduler
import org.gdglille.devfest.android.theme.m3.style.R
import org.gdglille.devfest.android.theme.m3.style.actions.TabActionsUi
import org.gdglille.devfest.repositories.AgendaRepository

@ExperimentalPagerApi
@ExperimentalCoroutinesApi
@FlowPreview
@Composable
fun AgendaVM(
    tabs: TabActionsUi,
    agendaRepository: AgendaRepository,
    alarmScheduler: AlarmScheduler,
    onTalkClicked: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(),
    viewModel: AgendaViewModel = viewModel(
        key = "${tabs.actions.count()}",
        factory = AgendaViewModel.Factory.create(
            tabs.actions.map { it.route },
            agendaRepository,
            alarmScheduler
        )
    )
) {
    val context = LocalContext.current
    val count = tabs.actions.count()
    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        is AgendaUiState.Loading -> Agenda(
            agenda = (uiState.value as AgendaUiState.Loading).agenda.first(),
            modifier = modifier,
            isLoading = true,
            onTalkClicked = {},
            onFavoriteClicked = { }
        )

        is AgendaUiState.Failure -> Text(text = stringResource(id = R.string.text_error))
        is AgendaUiState.Success -> {
            val agenda = (uiState.value as AgendaUiState.Success).agenda
            HorizontalPager(
                count = if (count == 0) 1 else agenda.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                Agenda(
                    agenda = agenda[page],
                    modifier = modifier,
                    onTalkClicked = onTalkClicked,
                    onFavoriteClicked = { talkItem ->
                        viewModel.markAsFavorite(context, talkItem)
                    }
                )
            }
        }
    }
}
