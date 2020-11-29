package com.word.search.puzzle.play.ui.game

import android.content.Context
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val gamesModule = module {
    viewModel { GameViewModel(get(), context = get<Context>()) }
}
