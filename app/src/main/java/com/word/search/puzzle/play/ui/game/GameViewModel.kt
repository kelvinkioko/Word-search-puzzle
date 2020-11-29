package com.word.search.puzzle.play.ui.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.constants.PreferenceHandler
import com.word.search.puzzle.play.database.entity.GridSizeEntity
import com.word.search.puzzle.play.database.entity.LanguageEntity
import com.word.search.puzzle.play.util.Event

class GameViewModel(private val preferenceHandler: PreferenceHandler, private val context: Context) : ViewModel() {

    private val _uiState = MutableLiveData<GameUIState>()
    val uiState: LiveData<GameUIState> = _uiState

    private val _action = MutableLiveData<Event<GameActions>>()
    val action: LiveData<Event<GameActions>> = _action

    fun shareApp(activity: Activity) {
        val googlePlayUrl = "https://play.google.com/store/apps/details?id="
        val msg = activity.getString(R.string.share_message) + " "

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg + googlePlayUrl + activity.packageName)
        shareIntent.type = "text/plain"
        activity.startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    fun rateApp(activity: Activity) {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${activity.packageName}")))
    }

    fun populateLanguages() {
        val languages = mutableListOf(
            LanguageEntity("Deutsch", "de"),
            LanguageEntity("English", "en"),
            LanguageEntity("Español", "es"),
            LanguageEntity("Français", "fr"),
            LanguageEntity("Italiano", "it"),
            LanguageEntity("Português", "pt")
        )
        _uiState.postValue(GameUIState.DisplayLanguages(languages = languages, selectedLanguage = preferenceHandler.getLanguage()!!))
    }

    fun setLanguage(language: String) {
        preferenceHandler.setLanguage(isLanguage = language)
    }

    fun populateGridSizes() {
        val grids = mutableListOf(
            GridSizeEntity("4x4", "4"),
            GridSizeEntity("5x5", "5"),
            GridSizeEntity("6x6", "6"),
            GridSizeEntity("7x7", "7"),
            GridSizeEntity("8x8", "8"),
            GridSizeEntity("9x9", "9"),
            GridSizeEntity("10x10", "10"),
            GridSizeEntity("11x11", "11"),
            GridSizeEntity("12x12", "12"),
            GridSizeEntity("13x13", "13"),
            GridSizeEntity("14x14", "14"),
            GridSizeEntity("15x15", "15")
        )
        _uiState.postValue(GameUIState.DisplayGridSizes(gridSizes = grids, selectedGridSize = preferenceHandler.getGridSize()!!))
    }

    fun setChosenGridSize(gridSize: String) {
        preferenceHandler.setGridSize(isGridSize = gridSize)
    }
}

sealed class GameActions {
    data class BottomNavigate(val bottomSheetDialogFragment: BottomSheetDialogFragment) : GameActions()
}

sealed class GameUIState {
    data class DisplayLanguages(val languages: List<LanguageEntity>, val selectedLanguage: String) : GameUIState()

    data class DisplayGridSizes(val gridSizes: List<GridSizeEntity>, val selectedGridSize: String) : GameUIState()
}
