package com.burujiyaseer.passwordmanager.domain.usecase.check_password_strength

import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength

interface CheckPasswordStrength {
    operator fun invoke(password: String): PasswordStrength
}