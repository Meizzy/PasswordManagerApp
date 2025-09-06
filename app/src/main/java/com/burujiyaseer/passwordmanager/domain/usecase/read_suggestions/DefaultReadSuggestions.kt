package com.burujiyaseer.passwordmanager.domain.usecase.read_suggestions

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultReadSuggestions @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : ReadSuggestions {
    override fun invoke(): Flow<Set<String>> {
        return preferencesDataSource.readSavedSuggestions()
    }
}