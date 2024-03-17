package com.burujiyaseer.passwordmanager.data.repository

import com.burujiyaseer.passwordmanager.data.local.PasswordManagerDao
import com.burujiyaseer.passwordmanager.di.coroutines.CoroutineDispatcherProvider
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultPasswordManagerRepository @Inject constructor(
    private val passwordManagerDao: PasswordManagerDao,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : PasswordManagerRepository {

    override suspend fun insertPasswordManager(passwordManagerModel: PasswordManagerModel) {
        withContext(coroutineDispatcherProvider.io()) {
            passwordManagerDao.insertPasswordManagerModel(passwordManagerModel)
        }
    }

    override suspend fun deletePasswordManagerById(entryId: String) {
        withContext(coroutineDispatcherProvider.io()) {
            passwordManagerDao.deletePasswordManagerModelById(entryId)
        }
    }

    override fun readPasswordEntryById(entryId: String): Flow<PasswordManagerModel?> =
        passwordManagerDao.readPasswordEntryById(entryId)

    override suspend fun getPasswordEntryById(entryId: String): PasswordManagerModel? =
        withContext(coroutineDispatcherProvider.io()) {
            passwordManagerDao.getPasswordEntryById(entryId)
        }

    override fun getAllPasswordEntries(): Flow<List<PasswordManagerModel>> =
        passwordManagerDao.getAllPasswordEntries()
}