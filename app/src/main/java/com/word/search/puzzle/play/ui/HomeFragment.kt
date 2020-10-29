package com.word.search.puzzle.play.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.FragmentHomeBinding
import com.word.search.puzzle.play.util.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            appVersion.text = "version - 0.0.0001"

            playButton.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toGameFragment())
            }

            settings.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toSettingsFragment())
            }
        }
    }
}
