package com.burujiyaseer.passwordmanager.domain.usecase.save_master_password

interface SaveMasterPassword {
    suspend operator fun invoke(masterPassword: String)
}