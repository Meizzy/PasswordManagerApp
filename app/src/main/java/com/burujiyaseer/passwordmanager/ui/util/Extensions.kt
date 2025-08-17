package com.burujiyaseer.passwordmanager.ui.util

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal fun utilLog(message: Any?) {
    Log.d("PasswordManagerTAG", message.toString())
}

@Composable
fun Modifier.surface() = this.background(MaterialTheme.colorScheme.surface)