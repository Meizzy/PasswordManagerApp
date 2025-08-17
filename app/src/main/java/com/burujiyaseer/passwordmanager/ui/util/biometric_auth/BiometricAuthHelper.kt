package com.burujiyaseer.passwordmanager.ui.util.biometric_auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface BiometricAuthHelper {
    suspend fun authenticate(fragment: Fragment): BiometricResult
    suspend fun authenticate(activity: FragmentActivity): BiometricResult
}