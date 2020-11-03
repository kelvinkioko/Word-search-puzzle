package com.word.search.puzzle.play.util

import android.content.Context
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.word.search.puzzle.play.ui.SharedViewModel

fun setupBannerAdView(context: Context): AdView {
    return AdView(context, "5586492314710123_5586553581370663", AdSize.BANNER_HEIGHT_50)
}

fun setupInterstitialAdView(context: Context): InterstitialAd {
    return InterstitialAd(context, "5586492314710123_5589544011071620")
}

fun setupInterstitialListener(interstitialAd: InterstitialAd, sharedViewModel: SharedViewModel): InterstitialAdListener {
    return object : InterstitialAdListener {
        override fun onInterstitialDisplayed(ad: Ad) {
            // Interstitial ad displayed callback
            println("Interstitial ad displayed.")
        }

        override fun onInterstitialDismissed(ad: Ad) {
            // Interstitial dismissed callback
            println("Interstitial ad dismissed.")
        }

        override fun onError(ad: Ad, adError: AdError) {
            // Ad error callback
            println("Interstitial ad failed to load: " + adError.errorMessage)
        }

        override fun onAdLoaded(ad: Ad) {
            // Interstitial ad is loaded and ready to be displayed
            println("Interstitial ad is loaded and ready to be displayed!")
            // Show the ad
            if (sharedViewModel.loadInterstitial.value.toString().equals("Show", ignoreCase = true)) {
                interstitialAd.show()
            }
        }

        override fun onAdClicked(ad: Ad) {
            // Ad clicked callback
            println("Interstitial ad clicked!")
        }

        override fun onLoggingImpression(ad: Ad) {
            // Ad impression logged callback
            println("Interstitial ad impression logged!")
        }
    }
}
