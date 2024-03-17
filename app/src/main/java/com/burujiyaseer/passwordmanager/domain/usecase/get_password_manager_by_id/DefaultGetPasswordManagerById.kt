package com.burujiyaseer.passwordmanager.domain.usecase.get_password_manager_by_id

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import javax.inject.Inject

class DefaultGetPasswordManagerById @Inject constructor(
    private val repository: PasswordManagerRepository
) : GetPasswordManagerById {
    override suspend fun invoke(entryId: String): PasswordManagerModel? =
        repository.getPasswordEntryById(entryId)
}