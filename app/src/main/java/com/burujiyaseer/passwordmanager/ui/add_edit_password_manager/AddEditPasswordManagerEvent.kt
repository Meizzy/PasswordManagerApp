package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

/**
 * wrapper interface to handle only important UI events
 */
sealed interface AddEditPasswordManagerEvent {
    data class AddedTitle(val title: String) : AddEditPasswordManagerEvent
    data class AddedPassword(val password: String) : AddEditPasswordManagerEvent
    data class AddedWebsite(val websiteUrl: String) : AddEditPasswordManagerEvent
    data class SavePasswordEntry(val uiPasswordModel: UIPasswordModel) : AddEditPasswordManagerEvent
    data object DeletePasswordEntryClicked : AddEditPasswordManagerEvent
    data object PositiveDeletePasswordEntry : AddEditPasswordManagerEvent
    data object LeavePasswordEntryClicked : AddEditPasswordManagerEvent
    data object PositiveLeavePasswordEntry : AddEditPasswordManagerEvent
    data object NegativeLeavePasswordEntry : AddEditPasswordManagerEvent
}