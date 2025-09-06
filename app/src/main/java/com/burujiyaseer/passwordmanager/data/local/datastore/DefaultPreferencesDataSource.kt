package com.burujiyaseer.passwordmanager.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.burujiyaseer.passwordmanager.di.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.LinkedHashSet

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

    override suspend fun saveEdDialogShownState() {
        withContext(dispatcherProvider.io()) {
            dataStore.edit { preferences ->
                preferences[ED_SHOWN_KEY] = true
            }
        }
    }

    override fun readEdDialogShownState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ED_SHOWN_KEY] ?: false
        }
    }

    override suspend fun saveSuggestion(suggestion: String) {
        withContext(dispatcherProvider.io()) {
            dataStore.edit { preferences ->
                val currentSuggestions = preferences[SUGGESTIONS_KEY] ?: emptySet()
                val newSuggestions = LinkedHashSet<String>(MAX_CAPACITY)//mapCapacity(currentSuggestions.size + 1))
                val addOperationOffset = if (newSuggestions.add(suggestion)) 1 else 0
                newSuggestions.addAll(currentSuggestions.take(MAX_CAPACITY - addOperationOffset))
                preferences[SUGGESTIONS_KEY] = newSuggestions
            }
        }
    }

    override suspend fun deleteSuggestion(suggestion: String) {
        withContext(dispatcherProvider.io()) {
            dataStore.edit { preferences ->
                val currentSuggestions = preferences[SUGGESTIONS_KEY] ?: emptySet()
                preferences[SUGGESTIONS_KEY] = currentSuggestions - suggestion
            }
        }
    }

    override fun readSavedSuggestions(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[SUGGESTIONS_KEY] ?: emptySet()
        }
    }

    private companion object {
        val MASTER_PASSWORD_KEY = stringPreferencesKey("master_password")
        val ED_SHOWN_KEY = booleanPreferencesKey("educational_dialog")
        val SUGGESTIONS_KEY = stringSetPreferencesKey("suggestion_history")
        private const val MAX_CAPACITY = 5
    }
}