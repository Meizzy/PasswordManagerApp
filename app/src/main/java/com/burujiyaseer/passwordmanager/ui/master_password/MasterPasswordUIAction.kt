package com.burujiyaseer.passwordmanager.ui.master_password

import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength

sealed interface MasterPasswordUIAction {
    data class ValidatePassword(val isCorrect: Boolean): MasterPasswordUIAction
    data class PasswordStrengthResult(val passwordStrength: PasswordStrength): MasterPasswordUIAction

}