package com.burujiyaseer.passwordmanager.ui.view_password_manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries.GetAllPasswordManagerEntries
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val SEARCH_DELAY = 500L
@HiltViewModel
class ViewPasswordsViewModel @Inject constructor(
    getAllPasswordManagerEntries: GetAllPasswordManagerEntries
) : ViewModel() {
    private val queryText = MutableStateFlow(EMPTY_STRING)

    @OptIn(FlowPreview::class)
    val passwordEntriesUIState = getAllPasswordManagerEntries().combine(
        queryText.debounce(SEARCH_DELAY)
    ) { passwordManagerModels, query ->
        utilLog("query: $query")
        val filteredQueries = if (query.isNotEmpty()) passwordManagerModels.filter { passwordManagerModel ->
            passwordManagerModel.title.contains(query, true)
        } else passwordManagerModels

        if (passwordManagerModels.isEmpty()) ViewPasswordUIAction.ShowEmptyListPrompt
        else if (filteredQueries.isEmpty()) ViewPasswordUIAction.ShowEmptySearchResults
        else ViewPasswordUIAction.SubmitData(filteredQueries)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )

    fun onQueryTextChanged(newText: String) {
        queryText.update { newText }
    }

}