package com.burujiyaseer.passwordmanager.ui.view_password_manager

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries.GetAllPasswordManagerEntries
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import com.burujiyaseer.passwordmanager.ui.util.Constants.PASSWORD_ENTRY_ID_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val SEARCH_DELAY = 500L
@HiltViewModel
class ViewPasswordsViewModel @Inject constructor(
    getAllPasswordManagerEntries: GetAllPasswordManagerEntries
) : ViewModel() {

    private val _uiActionFlow = MutableStateFlow<ViewPasswordUIAction?>(null)
    val uiActionFlow = _uiActionFlow.asStateFlow()
    private val queryText = MutableStateFlow(EMPTY_STRING)

    private val _navigationActionState =
        MutableStateFlow<ViewPasswordUIAction.NavigationAction?>(null)
    val navigationActionState = _navigationActionState.asStateFlow()
    private val navigationMutex = Mutex()

    @OptIn(FlowPreview::class)
    val passwordEntriesUIState = getAllPasswordManagerEntries().combine(
        queryText.debounce(SEARCH_DELAY)
    ) { passwordManagerModels, query ->
        val filteredQueries = if (query.isNotEmpty()) passwordManagerModels.filter { passwordManagerModel ->
            passwordManagerModel.title.contains(query, true)
        } else passwordManagerModels

        if (passwordManagerModels.isEmpty()) ViewPasswordUIAction.ShowEmptyListPrompt
        else if (filteredQueries.isEmpty()) ViewPasswordUIAction.ShowEmptySearchResults
        else ViewPasswordUIAction.SubmitData(filteredQueries)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    fun onAddPasswordClicked() {
        viewModelScope.launch {
            setupNavigationState()
        }
    }

    fun onPasswordEntryClicked(entryId: String) {
        viewModelScope.launch {
            setupNavigationState(
                bundleOf(PASSWORD_ENTRY_ID_KEY to entryId)
            )
        }
    }

    private suspend fun setupNavigationState(
        args: Bundle = Bundle.EMPTY
    ) {
        navigationMutex.withLock {
            if (navigationActionState.value == null) _navigationActionState.update {
                ViewPasswordUIAction.NavigationAction(
                    destinationId = R.id.addOrEditPasswordFragment,
                    args = args
                )
            }
        }
    }

    fun onNavigationHandled() {
        viewModelScope.launch {
            resetNavigationState()
        }
    }
    private suspend fun resetNavigationState() {
        navigationMutex.withLock {
            _navigationActionState.update { null }
        }
    }

    fun onQueryTextChanged(newText: String) {
        queryText.update { newText }
    }

}