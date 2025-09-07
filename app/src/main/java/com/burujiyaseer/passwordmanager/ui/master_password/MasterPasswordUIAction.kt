package com.burujiyaseer.passwordmanager.ui.master_password

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength

@Immutable
sealed interface MasterPasswordUIAction {
    @Immutable
    data class ValidatePassword(val isCorrect: Boolean): MasterPasswordUIAction
    data object ShowBiometricsDialog: MasterPasswordUIAction
    @Immutable
    data class PasswordStrengthResult(
        @param:StringRes val helperTextRes: Int,
        @param:ColorRes val helperTextColor: Int,
        val isWeak: Boolean
    ): MasterPasswordUIAction

}