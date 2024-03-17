package com.burujiyaseer.passwordmanager.ui.util.biometric_auth

import androidx.biometric.BiometricPrompt

/**
 * Sealed class to wrap [BiometricPrompt]'s [Int]-based return codes into more easily-interpreted
 * types.
 */
sealed interface BiometricResult {

    /** Biometric authentication was a success. */
    data class Success(val cryptoObject: BiometricPrompt.CryptoObject?) : BiometricResult

    /** Biometric authentication has irreversibly failed. */
    data class Failure(val code: Int?, val message: CharSequence) : BiometricResult

    /**
     * An incorrect biometric was entered, but the prompt UI is offering the option to retry the
     * operation.
     */
    data object Retry : BiometricResult

    /** The biometric hardware is unavailable or disabled on a software or hardware level. */
    data object HardwareUnavailableOrDisabled : BiometricResult

    /** The biometric prompt was canceled due to a user-initiated action. */
    data object CanceledByUser : BiometricResult

    /** The biometric prompt was canceled by the system. */
    data object CanceledBySystem : BiometricResult
}