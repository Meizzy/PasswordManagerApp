package com.burujiyaseer.passwordmanager.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.burujiyaseer.passwordmanager.di.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : PreferencesDataSource {

    override suspend fun saveMasterPassword(masterPassword: String) {
        withContext(dispatcherProvider.io()) {
            dataStore.edit { preferences ->
                preferences[MASTER_PASSWORD_KEY] = masterPassword
            }
        }
    }

    override suspend fun getMasterPassword(): String? = dataStore.data.map { preferences ->
        preferences[MASTER_PASSWORD_KEY]
    }.firstOrNull()

    override fun readMasterPassword(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[MASTER_PASSWORD_KEY]
    }

    override suspend fun clearSavedMasterPassword() {
        dataStore.edit { preferences ->
            preferences.remove(MASTER_PASSWORD_KEY)
        }
    }

    private companion object {
        val MASTER_PASSWORD_KEY = stringPreferencesKey("master_password")
    }
}