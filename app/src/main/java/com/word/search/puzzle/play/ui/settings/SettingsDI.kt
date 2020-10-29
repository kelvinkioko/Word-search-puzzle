package com.word.search.puzzle.play.ui.settings

import android.content.Context
import com.word.search.puzzle.play.constants.PreferenceHandler
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single { get<Context>().getSharedPreferences(PreferenceHandler.PREFS_FILE, Context.MODE_PRIVATE) }
    single { PreferenceHandler(sharedPreferences = get()) }
    viewModel { SettingsViewModel(preferenceHandler = get(), context = get<Context>()) }
}
