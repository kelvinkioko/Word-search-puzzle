package com.word.search.puzzle.play.database

import android.app.Application

class WordSearchDatabaseApp : Application() {

    private lateinit var instance: WordSearchDatabaseApp

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
