package com.burujiyaseer.passwordmanager.domain.usecase.insert_password_manager

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

interface InsertPasswordManager {
    suspend operator fun invoke(passwordManagerModel: PasswordManagerModel)
}