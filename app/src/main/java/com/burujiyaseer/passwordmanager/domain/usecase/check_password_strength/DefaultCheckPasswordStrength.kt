package com.burujiyaseer.passwordmanager.domain.usecase.check_password_strength

import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength
import javax.inject.Inject

private const val MIN_PASSWORD_LENGTH = 8
private const val ONLY_LETTERS_PATTERN = "[a-zA-Z]+"
private const val ONLY_DIGITS_PATTERN = "[0-9]+"

class DefaultCheckPasswordStrength @Inject constructor() : CheckPasswordStrength {
    override fun invoke(password: String): PasswordStrength {
        return if (isStrongPassword(password)) PasswordStrength.STRONG
        else if (isOkayPassword(password)) PasswordStrength.OKAY
        else PasswordStrength.WEAK
    }

    private fun isWeakPassword(password: String): Boolean {
        return password.length < MIN_PASSWORD_LENGTH || password.matches(Regex(ONLY_LETTERS_PATTERN))
                || password.matches(
            Regex(ONLY_DIGITS_PATTERN)
        )
    }

    private fun isOkayPassword(password: String): Boolean {
        return password.length >= 8 && password.any { it.isUpperCase() } && password.any { it.isLowerCase() }
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length > 12 && password.any { it.isUpperCase() } && password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() }
    }

}