package com.word.search.puzzle.play.ui.settings.wordcount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.database.entity.WordCountEntity
import com.word.search.puzzle.play.databinding.SettingsPreferenceChooserFragmentBinding
import com.word.search.puzzle.play.ui.settings.SettingsUIState
import com.word.search.puzzle.play.ui.settings.SettingsViewModel
import com.word.search.puzzle.play.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WordCountFragment : Fragment(R.layout.settings_preference_chooser_fragment) {

    private val binding by viewBinding(SettingsPreferenceChooserFragmentBinding::bind)

    private val viewModel: SettingsViewModel by viewModel()

    private val wordCountAdapter: WordCountAdapter by lazy {
        WordCountAdapter { onWordCountPicked(it) }
    }

    private fun onWordCountPicked(wordCountEntity: WordCountEntity) {
        viewModel.setWordCount(wordCount = wordCountEntity.word_count)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()
        setupViewObservers()
        setupTransactionTypesList()

        viewModel.populateWordCounts()
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
                is SettingsUIState.DisplayWordCounts -> populateWordCounts(
                        wordCountsEntity = it.wordCounts,
                        selectedWordCount = it.selectedWordCount
                )
            }
        }
    }

    private fun populateWordCounts(wordCountsEntity: List<WordCountEntity>, selectedWordCount: String) {
        wordCountAdapter.setWordCounts(wordCountsEntity, selectedWordCount = selectedWordCount)
    }

    private fun setupTransactionTypesList() {
        binding.itemsList.adapter = wordCountAdapter
    }
}
