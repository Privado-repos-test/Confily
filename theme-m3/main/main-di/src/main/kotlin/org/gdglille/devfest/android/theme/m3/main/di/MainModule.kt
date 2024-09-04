package org.gdglille.devfest.android.theme.m3.main.di

import com.paligot.confily.core.di.repositoriesModule
import com.paligot.confily.infos.di.infosModule
import com.paligot.confily.networking.di.networkingModule
import com.paligot.confily.partners.di.partnersModule
import com.paligot.confily.schedules.di.scheduleModule
import com.paligot.confily.speakers.di.speakersModule
import org.gdglille.devfest.android.theme.MainNavigationViewModel
import org.gdglille.devfest.android.theme.MainViewModel
import org.gdglille.devfest.android.theme.m3.events.di.eventListModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    includes(
        repositoriesModule,
        eventListModule,
        infosModule,
        networkingModule,
        partnersModule,
        scheduleModule,
        speakersModule
    )
    viewModel { MainNavigationViewModel(get(), get()) }
    viewModel { parameters -> MainViewModel(parameters.get(), get()) }
}
