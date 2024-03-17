package com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import kotlinx.coroutines.flow.Flow

interface GetAllPasswordManagerEntries {
    operator fun invoke(): Flow<List<PasswordManagerModel>>
}