package com.burujiyaseer.passwordmanager.domain.usecase.read_password_manager_by_id

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.EncryptDecryptPassword
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultReadPasswordManagerById @Inject constructor(
    private val repository: PasswordManagerRepository,
    private val encryptDecryptPassword: EncryptDecryptPassword
) : ReadPasswordManagerById {
    override fun invoke(entryId: String?): Flow<PasswordManagerModel?> {
        val passwordManagerModelFlow =
            repository.readPasswordEntryById(entryId ?: return emptyFlow())
        return passwordManagerModelFlow.map { passwordManagerModel ->
            passwordManagerModel?.copy(
                password = encryptDecryptPassword.decryptPassword(
                    passwordManagerModel.passwordFileName
                ) ?: EMPTY_STRING
            )
        }
    }

}