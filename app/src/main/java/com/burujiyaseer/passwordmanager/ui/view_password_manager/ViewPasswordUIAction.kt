package com.burujiyaseer.passwordmanager.ui.view_password_manager

import android.os.Bundle
import androidx.annotation.IdRes
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

sealed interface ViewPasswordUIAction {
    data class NavigationAction(
        @IdRes val destinationId: Int,
        val args: Bundle
    ) : ViewPasswordUIAction

    data class SubmitData(val filteredQueries: List<PasswordManagerModel>) : ViewPasswordUIAction
    data object ShowEmptyListPrompt : ViewPasswordUIAction
    data object ShowEmptySearchResults : ViewPasswordUIAction
}
