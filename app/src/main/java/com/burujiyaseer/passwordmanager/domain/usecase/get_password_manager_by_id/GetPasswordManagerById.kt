package com.burujiyaseer.passwordmanager.domain.usecase.get_password_manager_by_id

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

interface GetPasswordManagerById {
    suspend operator fun invoke(entryId: String): PasswordManagerModel?
}