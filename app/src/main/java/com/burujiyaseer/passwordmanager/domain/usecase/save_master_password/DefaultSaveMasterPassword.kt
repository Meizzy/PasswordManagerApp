package com.burujiyaseer.passwordmanager.domain.usecase.save_master_password

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import javax.inject.Inject

class DefaultSaveMasterPassword @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : SaveMasterPassword {
    override suspend fun invoke(masterPassword: String) {
        preferencesDataSource.saveMasterPassword(masterPassword)
    }
}