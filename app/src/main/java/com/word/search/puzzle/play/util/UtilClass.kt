package com.word.search.puzzle.play.util

import android.content.Context
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener

fun setupBannerAdView(context: Context): AdView {
    return AdView(context, "2623680264372670_4444174165656595", AdSize.BANNER_HEIGHT_50)
}

fun setupInterstitialAdView(context: Context): InterstitialAd {
    return InterstitialAd(context, "2623680264372670_4445452982195380")
}

fun setupInterstitialListener(interstitialAd: InterstitialAd): InterstitialAdListener {
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
            // interstitialAd.show()
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
