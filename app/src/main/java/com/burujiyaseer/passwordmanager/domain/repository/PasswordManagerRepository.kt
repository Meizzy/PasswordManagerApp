package com.burujiyaseer.passwordmanager.domain.repository

import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import kotlinx.coroutines.flow.Flow

interface PasswordManagerRepository {
    suspend fun insertPasswordManager(passwordManagerModel: PasswordManagerModel)
    suspend fun deletePasswordManagerById(entryId: String)
    fun readPasswordEntryById(entryId: String): Flow<PasswordManagerModel?>
    suspend fun getPasswordEntryById(entryId: String): PasswordManagerModel?
    fun getAllPasswordEntries() : Flow<List<PasswordManagerModel>>

}