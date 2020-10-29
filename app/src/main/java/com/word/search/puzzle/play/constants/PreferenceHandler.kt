package com.word.search.puzzle.play.constants

import android.content.SharedPreferences

class PreferenceHandler(private val sharedPreferences: SharedPreferences) {

    fun setLanguage(isLanguage: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("LanguagePref", isLanguage)
        editor.apply()
    }

    fun getLanguage(): String? {
        return sharedPreferences.getString("LanguagePref", "en")
    }

    fun setGridSize(isGridSize: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("GridSizePref", isGridSize)
        editor.apply()
    }

    fun getGridSize(): String? {
        return sharedPreferences.getString("GridSizePref", "5")
    }

    fun setRandomWordCount(isRandomWordCount: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("RandomWordCountPref", isRandomWordCount)
        editor.apply()
    }

    fun getRandomWordCount(): String? {
        return sharedPreferences.getString("RandomWordCountPref", "500")
    }

    fun setDeviceAwakePref(isDeviceAwakePref: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("DeviceAwakePref", isDeviceAwakePref)
        editor.apply()
    }

    fun getDeviceAwakePref(): Boolean {
        return sharedPreferences.getBoolean("DeviceAwakePref", true)
    }

    fun setRoundedPencilPref(isRoundedPencilPref: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("RoundedPencilPref", isRoundedPencilPref)
        editor.apply()
    }

    fun getRoundedPencilPref(): Boolean {
        return sharedPreferences.getBoolean("RoundedPencilPref", true)
    }

    companion object {
        const val PREFS_FILE = "WSPPreference"
    }
}
