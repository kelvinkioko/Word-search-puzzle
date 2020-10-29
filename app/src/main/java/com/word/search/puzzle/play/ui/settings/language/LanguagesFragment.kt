package com.word.search.puzzle.play.ui.settings.language

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.database.entity.LanguageEntity
import com.word.search.puzzle.play.databinding.SettingsPreferenceChooserFragmentBinding
import com.word.search.puzzle.play.ui.settings.SettingsUIState
import com.word.search.puzzle.play.ui.settings.SettingsViewModel
import com.word.search.puzzle.play.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LanguagesFragment : Fragment(R.layout.settings_preference_chooser_fragment) {

    private val binding by viewBinding(SettingsPreferenceChooserFragmentBinding::bind)

    private val viewModel: SettingsViewModel by viewModel()

    private val languageAdapter: LanguageAdapter by lazy {
        LanguageAdapter { onLanguagePicked(it) }
    }

    private fun onLanguagePicked(languageEntity: LanguageEntity) {
        viewModel.setLanguage(language = languageEntity.initial)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()
        setupViewObservers()
        setupTransactionTypesList()

        viewModel.populateLanguages()
    }

    private fun setupToolBar() {
        binding.apply {
            toolbarTitle.text = "Languages"
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupViewObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsUIState.DisplayLanguages -> populateLanguages(
                        languagesEntity = it.languages,
                        selectedLanguage = it.selectedLanguage
                )
            }
        }
    }

    private fun populateLanguages(languagesEntity: List<LanguageEntity>, selectedLanguage: String) {
        languageAdapter.setLanguages(languagesEntity, selectedLanguage = selectedLanguage)
    }

    private fun setupTransactionTypesList() {
        binding.itemsList.adapter = languageAdapter
    }
}
