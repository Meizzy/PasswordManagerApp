package com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultGetAllPasswordManagerEntries @Inject constructor(
    private val repository: PasswordManagerRepository
): GetAllPasswordManagerEntries {
    override fun invoke(): Flow<List<PasswordManagerModel>> =
        repository.getAllPasswordEntries()
}