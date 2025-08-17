package com.burujiyaseer.passwordmanager.domain.usecase.read_password_manager_by_id

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import kotlinx.coroutines.flow.Flow

interface ReadPasswordManagerById {
    /**
     * read entry with [entryId], and simultaneously decrypt the password by
     * the saved [PasswordManagerModel.passwordFileName]
     */
    suspend operator fun invoke(entryId: String): PasswordManagerModel?
}