package com.burujiyaseer.passwordmanager.domain.usecase.clear_saved_password

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import javax.inject.Inject

class DefaultClearSavedMasterPassword @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : ClearSavedMasterPassword {
    override suspend fun invoke() {
        preferencesDataSource.clearSavedMasterPassword()
    }
}