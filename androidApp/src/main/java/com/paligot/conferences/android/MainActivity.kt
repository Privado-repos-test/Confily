package com.paligot.conferences.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.paligot.conferences.android.screens.agenda.AgendaVM
import com.paligot.conferences.android.screens.schedule.ScheduleDetailVM
import com.paligot.conferences.android.screens.speakers.SpeakerDetailVM
import com.paligot.conferences.android.theme.ConferenceTheme
import com.paligot.conferences.network.ConferenceApi
import com.paligot.conferences.repositories.AgendaRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val baseUrl = "http://10.0.2.2:8080"
        val baseUrl = "https://cms4partners-ce427.nw.r.appspot.com"
        val repository = AgendaRepository.Factory.create(
            ConferenceApi.Factory.create(
                baseUrl = baseUrl, eventId = "AFtTtSMq1NY4tnK2gMg1", enableNetworkLogs = BuildConfig.DEBUG
            )
        )
        setContent {
            ConferenceTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val statusBarColor = MaterialTheme.colors.primary
                val navBarColor = MaterialTheme.colors.background
                SideEffect {
                    systemUiController.setSystemBarsColor(color = statusBarColor, darkIcons = useDarkIcons)
                    systemUiController.setNavigationBarColor(color = navBarColor, darkIcons = useDarkIcons)
                }
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "agenda") {
                    composable("agenda") {
                        AgendaVM(agendaRepository = repository, onTalkClicked = {
                            navController.navigate("schedules/$it")
                        })
                    }
                    composable(
                        route = "schedules/{scheduleId}",
                        arguments = listOf(navArgument("scheduleId") { type = NavType.StringType })
                    ) {
                        ScheduleDetailVM(scheduleId = it.arguments?.getString("scheduleId")!!,
                            agendaRepository = repository,
                            onBackClicked = {
                                navController.popBackStack()
                            },
                            onSpeakerClicked = {
                                navController.navigate("speakers/$it")
                            })
                    }
                    composable(
                        route = "speakers/{speakerId}",
                        arguments = listOf(navArgument("speakerId") { type = NavType.StringType })
                    ) {
                        SpeakerDetailVM(speakerId = it.arguments?.getString("speakerId")!!,
                            agendaRepository = repository,
                            onTwitterClick = {},
                            onGitHubClick = {},
                            onBackClicked = {
                                navController.popBackStack()
                            })
                    }
                }
            }
        }
    }
}
