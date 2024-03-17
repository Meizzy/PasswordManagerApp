package com.burujiyaseer.passwordmanager.domain.usecase.insert_password_manager

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import javax.inject.Inject

class DefaultInsertPasswordManager @Inject constructor(
    private val repository: PasswordManagerRepository
) : InsertPasswordManager {
    override suspend fun invoke(passwordManagerModel: PasswordManagerModel) {
        repository.insertPasswordManager(passwordManagerModel)
    }
}