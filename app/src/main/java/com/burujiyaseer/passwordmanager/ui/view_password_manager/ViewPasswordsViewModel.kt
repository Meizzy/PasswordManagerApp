package com.burujiyaseer.passwordmanager.ui.view_password_manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries.GetAllPasswordManagerEntries
import com.burujiyaseer.passwordmanager.domain.usecase.read_suggestions.ReadSuggestions
import com.burujiyaseer.passwordmanager.domain.usecase.save_suggestion.SaveSuggestion
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import com.burujiyaseer.passwordmanager.ui.util.StandardWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewPasswordsViewModel @Inject constructor(
    getAllPasswordManagerEntries: GetAllPasswordManagerEntries,
    readSuggestions: ReadSuggestions,
    private val saveSuggestion: SaveSuggestion
) : ViewModel() {

    /**
     * the submitted search query
     */
    private val queryText = MutableStateFlow(EMPTY_STRING)

    /**
     * the suggested query
     */
    private val suggestionText = MutableStateFlow(EMPTY_STRING)

    private val allPasswordEntriesFlow = getAllPasswordManagerEntries()
    private val savedSuggestionsFlow = readSuggestions()

    val passwordEntriesUIState = allPasswordEntriesFlow.combine(
        queryText
    ) { passwordManagerModels, query ->
        val filteredQueries =
            if (query.isNotEmpty()) passwordManagerModels.filter { passwordManagerModel ->
                passwordManagerModel.title.contains(query, true)
            } else passwordManagerModels

        if (passwordManagerModels.isEmpty()) ViewPasswordUIAction.ShowEmptyListPrompt
        else if (filteredQueries.isEmpty()) ViewPasswordUIAction.ShowEmptySearchResults
        else ViewPasswordUIAction.SubmitData(filteredQueries)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.StandardWhileSubscribed(),
            null
        )

    val suggestionsUIState = combine(
        savedSuggestionsFlow,
        suggestionText,
    ) { savedSuggestions, query ->
        val passwordManagerModels = allPasswordEntriesFlow.first()
        val filteredSuggestions =
            if (query.isNotEmpty()) passwordManagerModels.mapNotNull { passwordManagerModel ->
                if (passwordManagerModel.title.contains(query, true)) passwordManagerModel.title
                else null
            } else savedSuggestions
        filteredSuggestions.map { suggestion ->
            SuggestionModel(suggestion, query.isEmpty())
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.StandardWhileSubscribed(),
            emptyList()
        )

    fun onQueryTextSubmitted(newText: String) {
        viewModelScope.launch {
            queryText.update { newText }
            if (newText.isNotBlank()) saveSuggestion(newText)
        }
    }

    fun onSuggestionTextChanged(newText: String) {
        suggestionText.update { newText }
    }

    fun onRemovedSuggestion(suggestion: String) {
        viewModelScope.launch {
            saveSuggestion.deleteSuggestion(suggestion)
        }
    }
}