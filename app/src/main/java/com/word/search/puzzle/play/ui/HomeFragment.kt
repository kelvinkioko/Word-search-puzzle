package com.word.search.puzzle.play.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.FragmentHomeBinding
import com.word.search.puzzle.play.ui.game.GameViewModel
import com.word.search.puzzle.play.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel: GameViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            appVersion.text = "version - 1.0.0"

            playButton.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toGameFragment())
            }

            settings.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toSettingsFragment())
            }

            rating.setOnClickListener {
                viewModel.rateApp(requireActivity())
            }

            ratingShare.setOnClickListener {
                viewModel.shareApp(requireActivity())
            }
        }
    }
}
