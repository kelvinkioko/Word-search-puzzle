package com.word.search.puzzle.play.ui.game

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val gamesModule = module {
    viewModel { GameViewModel() }
}
