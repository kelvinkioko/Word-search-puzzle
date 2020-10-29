package com.word.search.puzzle.play.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
