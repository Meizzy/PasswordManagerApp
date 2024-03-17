package com.burujiyaseer.passwordmanager.domain.usecase.delete_password_manager_entry

interface DeletePasswordManagerEntry {
    suspend operator fun invoke(entryId: String)
}