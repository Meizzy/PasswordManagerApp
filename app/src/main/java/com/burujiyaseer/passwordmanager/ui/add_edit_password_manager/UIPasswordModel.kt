package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

data class UIPasswordModel(
    val entryId: String? = null,
    var title: String = "",
    var account: String = "",
    var username: String = "",
    var password: String = "",
    var websiteUrl: String = "",
    var description: String = ""
)
