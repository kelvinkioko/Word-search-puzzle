package com.word.search.puzzle.play.ui.settings

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
import com.word.search.puzzle.play.database.entity.WordCountEntity
import com.word.search.puzzle.play.util.Event

class SettingsViewModel(private val preferenceHandler: PreferenceHandler, private val context: Context) : ViewModel() {

    private val _uiState = MutableLiveData<SettingsUIState>()
    val uiState: LiveData<SettingsUIState> = _uiState

    private val _action = MutableLiveData<Event<SettingsActions>>()
    val action: LiveData<Event<SettingsActions>> = _action

    init {
        println("Grid size ${preferenceHandler.getGridSize()!!}")
        _uiState.postValue(SettingsUIState.InitPrefs(preferenceHandler.getDeviceAwakePref(), preferenceHandler.getRoundedPencilPref()))
    }

    fun shareApp() {
        val googlePlayUrl = "https://play.google.com/store/apps/details?id="
        val msg = context.getString(R.string.review_message) + " "

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg + googlePlayUrl + context.packageName)
        shareIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    fun rateApp() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
    }

    fun setKeepDeviceAwake() {
        preferenceHandler.setDeviceAwakePref(!preferenceHandler.getDeviceAwakePref())
        _uiState.postValue(SettingsUIState.DeviceAwake(preferenceHandler.getDeviceAwakePref()))
    }

    fun setRoundedPencil() {
        preferenceHandler.setRoundedPencilPref(!preferenceHandler.getRoundedPencilPref())
        _uiState.postValue(SettingsUIState.RoundedPencil(preferenceHandler.getRoundedPencilPref()))
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
        _uiState.postValue(SettingsUIState.DisplayLanguages(languages = languages, selectedLanguage = preferenceHandler.getLanguage()!!))
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
                GridSizeEntity("10x10", "10")
        )
        _uiState.postValue(SettingsUIState.DisplayGridSizes(gridSizes = grids, selectedGridSize = preferenceHandler.getGridSize()!!))
    }

    fun setChosenGridSize(gridSize: String) {
        preferenceHandler.setGridSize(isGridSize = gridSize)
    }

    fun populateWordCounts() {
        val wordCount = mutableListOf(
                WordCountEntity("500"),
                WordCountEntity("1000"),
                WordCountEntity("2000"),
                WordCountEntity("3000"),
                WordCountEntity("4000"),
                WordCountEntity("5000")
        )
        _uiState.postValue(SettingsUIState.DisplayWordCounts(wordCounts = wordCount, selectedWordCount = preferenceHandler.getRandomWordCount()!!))
    }

    fun setWordCount(wordCount: String) {
        preferenceHandler.setRandomWordCount(isRandomWordCount = wordCount)
    }
}

sealed class SettingsActions {
    data class BottomNavigate(val bottomSheetDialogFragment: BottomSheetDialogFragment) : SettingsActions()
}

sealed class SettingsUIState {
    data class InitPrefs(val isDeviceAwake: Boolean, val isRoundedPencil: Boolean) : SettingsUIState()

    data class DeviceAwake(val isDeviceAwake: Boolean) : SettingsUIState()

    data class RoundedPencil(val isRoundedPencil: Boolean) : SettingsUIState()

    data class DisplayLanguages(val languages: List<LanguageEntity>, val selectedLanguage: String) : SettingsUIState()

    data class DisplayGridSizes(val gridSizes: List<GridSizeEntity>, val selectedGridSize: String) : SettingsUIState()

    data class DisplayWordCounts(val wordCounts: List<WordCountEntity>, val selectedWordCount: String) : SettingsUIState()
}
