package com.word.search.puzzle.play.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val loadInterstitial = MutableLiveData<Any>()

    fun setLoadInterstitial(loadInterstitial: String) {
        /* The Options are
         *  1. Wait
         *  2. Prepare
         *  3. Show
         */

        this.loadInterstitial.value = loadInterstitial
    }
}
