package com.word.search.puzzle.play.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.FragmentSettingsBinding
import com.word.search.puzzle.play.util.observeEvent
import com.word.search.puzzle.play.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObserver()
    }

    private fun setupClickListeners() {
        binding.apply {
            languageSetting.setOnClickListener { findNavController().navigate(SettingsFragmentDirections.toLanguagesFragment()) }
            gridSize.setOnClickListener { findNavController().navigate(SettingsFragmentDirections.toGridSizeFragment()) }
            randomWordCount.setOnClickListener { findNavController().navigate(SettingsFragmentDirections.toWordCountFragment()) }
            keepDeviceAwake.setOnClickListener { viewModel.setKeepDeviceAwake() }
            roundedPencil.setOnClickListener { viewModel.setRoundedPencil() }
        }
    }

    private fun setupObserver() {
        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is SettingsActions.BottomNavigate -> showBottomSheetDialog(it.bottomSheetDialogFragment)
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsUIState.InitPrefs -> {
                    binding.apply {
                        keepDeviceAwake.setItemChecked(checked = it.isDeviceAwake)
                        roundedPencil.setItemChecked(checked = it.isRoundedPencil)
                    }
                }
                is SettingsUIState.DeviceAwake -> binding.keepDeviceAwake.setItemChecked(checked = it.isDeviceAwake)
                is SettingsUIState.RoundedPencil -> binding.roundedPencil.setItemChecked(checked = it.isRoundedPencil)
            }
        }
    }

    private fun showBottomSheetDialog(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        bottomSheetDialogFragment.show(parentFragmentManager, bottomSheetDialogFragment.tag)
    }
}
