package com.burujiyaseer.passwordmanager.domain.usecase.read_password_manager_by_id

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.EncryptDecryptPassword
import javax.inject.Inject

private const val EMPTY_STRING = ""

class DefaultReadPasswordManagerById @Inject constructor(
    private val repository: PasswordManagerRepository,
    private val encryptDecryptPassword: EncryptDecryptPassword
) : ReadPasswordManagerById {
    override suspend fun invoke(entryId: String): PasswordManagerModel? {
        val passwordManagerModel = repository.getPasswordEntryById(entryId)
        return passwordManagerModel?.copy(
            password = encryptDecryptPassword.decryptPassword(
                passwordManagerModel.passwordFileName
            ) ?: EMPTY_STRING
        )
    }

}