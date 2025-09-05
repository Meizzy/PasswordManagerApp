package com.burujiyaseer.passwordmanager.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    suspend fun saveMasterPassword(masterPassword: String)
    suspend fun getMasterPassword(): String?
    fun readMasterPassword(): Flow<String?>
    suspend fun clearSavedMasterPassword()
    suspend fun saveEdDialogShownState()
    fun readEdDialogShownState(): Flow<Boolean>
}