package com.burujiyaseer.passwordmanager.ui.master_password

import androidx.compose.runtime.Immutable
import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength

@Immutable
sealed interface MasterPasswordUIAction {
    @Immutable
    data class ValidatePassword(val isCorrect: Boolean): MasterPasswordUIAction
    data object ShowBiometricsDialog: MasterPasswordUIAction
    @Immutable
    data class PasswordStrengthResult(val passwordStrength: PasswordStrength): MasterPasswordUIAction

}