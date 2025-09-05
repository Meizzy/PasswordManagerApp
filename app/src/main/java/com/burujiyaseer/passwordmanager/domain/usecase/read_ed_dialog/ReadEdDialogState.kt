package com.burujiyaseer.passwordmanager.domain.usecase.read_ed_dialog

import kotlinx.coroutines.flow.Flow

interface ReadEdDialogState {
    operator fun invoke(): Flow<Boolean>
}