package com.burujiyaseer.passwordmanager.domain.usecase.save_suggestion

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import javax.inject.Inject

class DefaultSaveSuggestion @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : SaveSuggestion {
    override suspend fun invoke(text: String) {
        preferencesDataSource.saveSuggestion(text)
    }

    override suspend fun deleteSuggestion(text: String) {
        preferencesDataSource.deleteSuggestion(text)
    }
}