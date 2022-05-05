package org.gdglille.devfest.android.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.gdglille.devfest.android.components.appbars.ActionItemId
import org.gdglille.devfest.android.components.appbars.BottomAppBar
import org.gdglille.devfest.android.components.appbars.TopAppBar
import org.gdglille.devfest.android.screens.Screen
import org.gdglille.devfest.android.screens.agenda.AgendaVM
import org.gdglille.devfest.android.screens.event.EventVM
import org.gdglille.devfest.android.screens.users.NetworkingVM
import org.gdglille.devfest.repositories.AgendaRepository
import org.gdglille.devfest.repositories.UserRepository

@Composable
fun Home(
    agendaRepository: AgendaRepository,
    userRepository: UserRepository,
    modifier: Modifier = Modifier,
    startDestination: Screen = Screen.Agenda,
    navController: NavHostController = rememberNavController(),
    onTalkClicked: (id: String) -> Unit,
    onFaqClick: (url: String) -> Unit,
    onCoCClick: (url: String) -> Unit,
    onTwitterClick: (url: String?) -> Unit,
    onLinkedInClick: (url: String?) -> Unit,
    onPartnerClick: (siteUrl: String?) -> Unit,
    onScannerClicked: () -> Unit,
    onQrCodeClicked: () -> Unit,
    onReportClicked: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val screen = currentDestination?.route?.getScreen() ?: startDestination
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(id = screen.title),
                actions = screen.actions,
                onActionClicked = {
                    when (it) {
                        ActionItemId.QrCodeScannerActionItem -> onScannerClicked()
                        ActionItemId.QrCodeActionItem -> onQrCodeClicked()
                        ActionItemId.ReportActionItem -> onReportClicked()
                        else -> TODO()
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                selected = screen,
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
    ) {
        NavHost(navController, startDestination = Screen.Agenda.route, modifier = modifier.padding(it)) {
            composable(Screen.Agenda.route) {
                AgendaVM(
                    agendaRepository = agendaRepository,
                    onTalkClicked = onTalkClicked,
                )
            }
            composable(Screen.Networking.route) {
                NetworkingVM(userRepository = userRepository)
            }
            composable(Screen.Event.route) {
                EventVM(
                    agendaRepository = agendaRepository,
                    onFaqClick = onFaqClick,
                    onCoCClick = onCoCClick,
                    onTwitterClick = onTwitterClick,
                    onLinkedInClick = onLinkedInClick,
                    onPartnerClick = onPartnerClick
                )
            }
        }
    }
}

internal fun String.getScreen(): Screen = when (this) {
    Screen.Agenda.route -> Screen.Agenda
    Screen.Event.route -> Screen.Event
    Screen.Networking.route -> Screen.Networking
    else -> TODO()
}
