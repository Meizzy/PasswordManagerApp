package com.burujiyaseer.passwordmanager.ui.view_password_manager

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

sealed interface ViewPasswordUIAction {
    data class SubmitData(val filteredQueries: List<PasswordManagerModel>) : ViewPasswordUIAction
    data object ShowEmptyListPrompt : ViewPasswordUIAction
    data object ShowEmptySearchResults : ViewPasswordUIAction
}
