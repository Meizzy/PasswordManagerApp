package com.burujiyaseer.passwordmanager.domain.usecase.read_master_password

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultReadMasterPassword @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : ReadMasterPassword {
    override fun invoke(): Flow<String?> {
        return preferencesDataSource.readMasterPassword()
    }
}