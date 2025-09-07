package com.burujiyaseer.passwordmanager.domain.usecase.show_ed_dialog

import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import javax.inject.Inject

class DefaultSaveShownEdDialog @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : SaveShownEdDialog {
    override suspend fun invoke() {
        preferencesDataSource.saveEdDialogShownState()
    }
}