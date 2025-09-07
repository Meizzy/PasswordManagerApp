package com.burujiyaseer.passwordmanager.ui.util.biometric_auth

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow

interface BiometricAuthHelper {
    fun authenticate(fragment: Fragment): Flow<BiometricResult>
    fun authenticate(activity: FragmentActivity): Flow<BiometricResult>
}