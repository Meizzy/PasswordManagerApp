package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

import androidx.annotation.StringRes

sealed interface AddEditPasswordManagerAction {
    data class ToggleDoneBtnEnabled(val isEnabled: Boolean) : AddEditPasswordManagerAction
    data class FillUIFields(
        val uiPasswordModel: UIPasswordModel,
        @StringRes val titleResId: Int,
        val addDeleteMenu: Boolean
    ) : AddEditPasswordManagerAction
    data object ShowLoader : AddEditPasswordManagerAction
    data object NavigateBack : AddEditPasswordManagerAction
}
