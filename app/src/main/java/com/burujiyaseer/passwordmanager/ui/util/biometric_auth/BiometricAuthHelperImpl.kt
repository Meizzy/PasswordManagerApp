package com.burujiyaseer.passwordmanager.ui.util.biometric_auth

import android.app.KeyguardManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val VALID_AUTHENTICATORS =
    BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK
class BiometricAuthHelperImpl : BiometricAuthHelper {

        override suspend fun authenticate(
            fragment: Fragment,
        ): BiometricResult = suspendCoroutine { continuation ->
            val activity = fragment.requireActivity()
            val authCallback = createPromptAuthenticationCallback(continuation)
            val deviceHasKeyguard =
                activity.getSystemService<KeyguardManager>()?.isDeviceSecure == true
            if (canAuthenticate(fragment) || deviceHasKeyguard) {
                val promptInfo =
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(fragment.getString(R.string.biometric_or_screen_lock_message))
                        .setAllowedAuthenticators(VALID_AUTHENTICATORS)
                        .build()
                BiometricPrompt(
                    fragment,
                    ContextCompat.getMainExecutor(fragment.requireContext()),
                    authCallback,
                )
                    .authenticate(promptInfo)
            } else {
                continuation.resume(BiometricResult.HardwareUnavailableOrDisabled)
            }
        }

        private fun createPromptAuthenticationCallback(
            continuation: Continuation<BiometricResult>,
        ): BiometricPrompt.AuthenticationCallback {
            return object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        /** Keep in sync with [androidx.biometric.BiometricPrompt.AuthenticationError] */
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> continuation.resume(BiometricResult.HardwareUnavailableOrDisabled)
                        BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> continuation.resume(BiometricResult.Retry)
                        BiometricPrompt.ERROR_TIMEOUT ->
                            continuation.resume(
                                BiometricResult.Failure(
                                    errorCode,
                                    errString
                                )
                            )
                        BiometricPrompt.ERROR_NO_SPACE ->
                            continuation.resume(
                                BiometricResult.Failure(
                                    errorCode,
                                    errString
                                )
                            )
                        BiometricPrompt.ERROR_CANCELED -> continuation.resume(BiometricResult.CanceledBySystem)
                        BiometricPrompt.ERROR_LOCKOUT ->
                            continuation.resume(
                                BiometricResult.Failure(
                                    errorCode,
                                    errString
                                )
                            )
                        BiometricPrompt.ERROR_VENDOR ->
                            continuation.resume(
                                BiometricResult.Failure(
                                    errorCode,
                                    errString
                                )
                            )
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                            continuation.resume(
                                BiometricResult.Failure(
                                    errorCode,
                                    errString
                                )
                            )
                        BiometricPrompt.ERROR_USER_CANCELED -> continuation.resume(BiometricResult.CanceledByUser)
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> continuation.resume(BiometricResult.HardwareUnavailableOrDisabled)
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> continuation.resume(BiometricResult.HardwareUnavailableOrDisabled)
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> continuation.resume(BiometricResult.CanceledByUser)
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL ->
                            continuation.resume(BiometricResult.HardwareUnavailableOrDisabled)
                        // We cover all guaranteed values above, but [errorCode] is still an Int
                        // at the end of the day so a catch-all else will always be required.
                        else -> {
                            continuation.resume(
                                BiometricResult.Failure(
                                    errorCode,
                                    errString
                                )
                            )
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    continuation.resume(BiometricResult.Retry)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    utilLog( "onAuthenticationSucceeded()" )
                    continuation.resume(BiometricResult.Success(result.cryptoObject))
                }
            }
        }

    private fun canAuthenticate(fragment: Fragment): Boolean {
        return BiometricManager.from(fragment.requireContext()).canAuthenticate(VALID_AUTHENTICATORS) ==
                BiometricManager.BIOMETRIC_SUCCESS

    }
}