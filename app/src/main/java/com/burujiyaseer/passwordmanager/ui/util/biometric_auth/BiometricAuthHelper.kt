package com.burujiyaseer.passwordmanager.ui.util.biometric_auth

import androidx.fragment.app.Fragment

interface BiometricAuthHelper {
    suspend fun authenticate(fragment: Fragment): BiometricResult
}