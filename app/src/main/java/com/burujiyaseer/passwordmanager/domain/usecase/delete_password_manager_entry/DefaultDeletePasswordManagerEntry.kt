package com.burujiyaseer.passwordmanager.domain.usecase.delete_password_manager_entry

import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import javax.inject.Inject

class DefaultDeletePasswordManagerEntry @Inject constructor(
    private val repository: PasswordManagerRepository
): DeletePasswordManagerEntry {
    override suspend fun invoke(entryId: String) {
        repository.deletePasswordManagerById(entryId)
    }
}