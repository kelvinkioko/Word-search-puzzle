package com.word.search.puzzle.play.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.ads.AdView
import com.facebook.ads.InterstitialAd
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.ActivityMainBinding
import com.word.search.puzzle.play.util.setupBannerAdView
import com.word.search.puzzle.play.util.setupInterstitialAdView
import com.word.search.puzzle.play.util.setupInterstitialListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedViewModel: SharedViewModel

    private var adView: AdView? = null

    private var interstitialAd: InterstitialAd? = null

    private var resume = true

    private var resumeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
            window.insetsController!!.hide(WindowInsets.Type.navigationBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        sharedViewModel = run { ViewModelProvider(this).get(SharedViewModel::class.java) }

        sharedViewModel.loadInterstitial.observe(this, Observer {
            when {
                it.toString().equals("Wait", ignoreCase = true) -> {}
                it.toString().equals("Prepare", ignoreCase = true) -> loadInterstitial()
                it.toString().equals("Show", ignoreCase = true) -> {
                    // Check if interstitialAd has been loaded successfully
                    // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                    if (interstitialAd != null && interstitialAd!!.isAdLoaded && !interstitialAd!!.isAdInvalidated) {
                        // Show the ad
                        val updateHandler = Handler()
                        val runnable = Runnable {
                            interstitialAd!!.show()
                        }
                        updateHandler.postDelayed(runnable, 1500)
                    }
                }
            }
        })

        loadBanner()
    }

    override fun onStart() {
        super.onStart()
        if (resume && resumeCount > 0) {
            loadBanner()
            resume = false
        }
        resumeCount++
    }

    override fun onStop() {
        super.onStop()
        if (interstitialAd != null) {
            interstitialAd!!.destroy()
        }
        if (adView != null) {
            adView!!.destroy()
        }
    }

    private fun loadInterstitial() {
        // Instantiate an InterstitialAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        interstitialAd = setupInterstitialAdView(this)
        // Create listeners for the Interstitial Ad
        val interstitialAdListener = setupInterstitialListener(interstitialAd!!, sharedViewModel)

        // For auto play video ads, it's recommended to load the ad at least 30 seconds before it is shown
        interstitialAd?.loadAd(interstitialAd?.buildLoadAdConfig()!!.withAdListener(interstitialAdListener).build())
    }

    private fun loadBanner() {
        // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.
        adView = setupBannerAdView(this@MainActivity)

        // Add the ad view to your activity layout
        binding.adView.addView(adView)

        // Request an ad
        adView!!.loadAd()
    }
}
