package com.word.search.puzzle.play.ui.settings.gridsize

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.database.entity.GridSizeEntity
import com.word.search.puzzle.play.databinding.SettingsPreferenceChooserFragmentBinding
import com.word.search.puzzle.play.ui.settings.SettingsUIState
import com.word.search.puzzle.play.ui.settings.SettingsViewModel
import com.word.search.puzzle.play.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class GridSizeFragment : Fragment(R.layout.settings_preference_chooser_fragment) {

    private val binding by viewBinding(SettingsPreferenceChooserFragmentBinding::bind)

    private val viewModel: SettingsViewModel by viewModel()

    private val gridSizeAdapter: GridSizeAdapter by lazy {
        GridSizeAdapter { onGridSizePicked(it) }
    }

    private fun onGridSizePicked(gridSizeEntity: GridSizeEntity) {
        viewModel.setChosenGridSize(gridSize = gridSizeEntity.grid_value)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()
        setupViewObservers()
        setupTransactionTypesList()

        viewModel.populateGridSizes()
    }

    private fun setupToolBar() {
        binding.apply {
            toolbarTitle.text = "Grid sizes"
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupViewObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsUIState.DisplayGridSizes -> populateGridSizes(
                        gridSizesEntity = it.gridSizes,
                        selectedGridSize = it.selectedGridSize
                )
            }
        }
    }

    private fun populateGridSizes(gridSizesEntity: List<GridSizeEntity>, selectedGridSize: String) {
        gridSizeAdapter.setGridSizes(gridSizesEntity, selectedGridSize = selectedGridSize)
    }

    private fun setupTransactionTypesList() {
        binding.itemsList.adapter = gridSizeAdapter
    }
}
