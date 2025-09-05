package com.burujiyaseer.passwordmanager.domain.usecase.read_ed_dialog

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultReadEdDialogState @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : ReadEdDialogState {
    override fun invoke(): Flow<Boolean> {
        return preferencesDataSource.readEdDialogShownState()
    }
}