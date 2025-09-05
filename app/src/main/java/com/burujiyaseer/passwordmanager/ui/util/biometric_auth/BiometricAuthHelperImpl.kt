package com.burujiyaseer.passwordmanager.ui.util.biometric_auth

import android.app.KeyguardManager
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val VALID_AUTHENTICATORS =
    BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK

class BiometricAuthHelperImpl : BiometricAuthHelper {

    override fun authenticate(
        fragment: Fragment,
    ): Flow<BiometricResult> = callbackFlow {
        val activity = fragment.requireActivity()
        val authCallback = createPromptAuthenticationCallback(channel)
        val deviceHasKeyguard =
            activity.getSystemService<KeyguardManager>()?.isDeviceSecure == true
        if (canAuthenticate(fragment.requireContext()) || deviceHasKeyguard) {
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
            channel.trySend(BiometricResult.HardwareUnavailableOrDisabled)
        }
    }

    override fun authenticate(
        activity: FragmentActivity
    ): Flow<BiometricResult> = callbackFlow {
        val authCallback = createPromptAuthenticationCallback(channel)
        val deviceHasKeyguard =
            activity.getSystemService<KeyguardManager>()?.isDeviceSecure == true
        if (canAuthenticate(activity) || deviceHasKeyguard) {
            val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(activity.getString(R.string.biometric_or_screen_lock_message))
                    .setAllowedAuthenticators(VALID_AUTHENTICATORS)
                    .build()
            BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                authCallback,
            )
                .authenticate(promptInfo)
        } else {
            channel.trySend(BiometricResult.HardwareUnavailableOrDisabled)
        }
    }

    private fun createPromptAuthenticationCallback(
        channel: SendChannel<BiometricResult>,
    ): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                utilLog("onAuthError: $errString")
                when (errorCode) {
                    /** Keep in sync with [androidx.biometric.BiometricPrompt.AuthenticationError] */
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> channel.trySend(BiometricResult.HardwareUnavailableOrDisabled)
                    BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> channel.trySend(BiometricResult.Retry)
                    BiometricPrompt.ERROR_TIMEOUT ->
                        channel.trySend(
                            BiometricResult.Failure(
                                errorCode,
                                errString
                            )
                        )

                    BiometricPrompt.ERROR_NO_SPACE ->
                        channel.trySend(
                            BiometricResult.Failure(
                                errorCode,
                                errString
                            )
                        )

                    BiometricPrompt.ERROR_CANCELED -> channel.trySend(BiometricResult.CanceledBySystem)
                    BiometricPrompt.ERROR_LOCKOUT ->
                        channel.trySend(
                            BiometricResult.Failure(
                                errorCode,
                                errString
                            )
                        )

                    BiometricPrompt.ERROR_VENDOR ->
                        channel.trySend(
                            BiometricResult.Failure(
                                errorCode,
                                errString
                            )
                        )

                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                        channel.trySend(
                            BiometricResult.Failure(
                                errorCode,
                                errString
                            )
                        )

                    BiometricPrompt.ERROR_USER_CANCELED -> channel.trySend(BiometricResult.CanceledByUser)
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> channel.trySend(BiometricResult.HardwareUnavailableOrDisabled)
                    BiometricPrompt.ERROR_HW_NOT_PRESENT -> channel.trySend(BiometricResult.HardwareUnavailableOrDisabled)
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> channel.trySend(BiometricResult.CanceledByUser)
                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL ->
                        channel.trySend(BiometricResult.HardwareUnavailableOrDisabled)
                    // We cover all guaranteed values above, but [errorCode] is still an Int
                    // at the end of the day so a catch-all else will always be required.
                    else -> {
                        channel.trySend(
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
                channel.trySend(BiometricResult.Retry)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                utilLog("onAuthenticationSucceeded()")
                channel.trySend(BiometricResult.Success(result.cryptoObject))
            }
        }
    }

    private fun canAuthenticate(context: Context): Boolean {
        return BiometricManager.from(context).canAuthenticate(VALID_AUTHENTICATORS) ==
                BiometricManager.BIOMETRIC_SUCCESS

    }
}