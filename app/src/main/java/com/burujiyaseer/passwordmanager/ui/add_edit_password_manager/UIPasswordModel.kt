package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

data class UIPasswordModel(
    val entryId: String?,
    val title: String,
    val account: String,
    val username: String,
    val password: String,
    val websiteUrl: String,
    val description: String
)
