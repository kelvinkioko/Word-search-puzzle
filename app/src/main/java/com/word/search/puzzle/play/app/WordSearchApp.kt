package com.word.search.puzzle.play.app

import android.app.Application
import com.facebook.ads.AudienceNetworkAds
import com.word.search.puzzle.play.di.environmentModule
import com.word.search.puzzle.play.ui.game.gamesModule
import com.word.search.puzzle.play.ui.settings.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WordSearchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this)

        startKoin {
            androidContext(this@WordSearchApp)
            modules(
                environmentModule,
                settingsModule,
                gamesModule
            )
        }
    }
}
