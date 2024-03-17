package com.burujiyaseer.passwordmanager.domain.usecase.read_master_password

import kotlinx.coroutines.flow.Flow

interface ReadMasterPassword {
    operator fun invoke(): Flow<String?>
}