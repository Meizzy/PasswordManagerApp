package com.burujiyaseer.passwordmanager.ui.util

import android.content.res.Configuration
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.util.Consumer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

internal fun utilLog(message: Any?) {
    Log.d("PasswordManagerTAG", message.toString())
}

@Composable
fun Modifier.surface() = this.background(MaterialTheme.colorScheme.surface)

typealias DefaultLambda = () -> Unit
/**
 * Convenience wrapper for dark mode checking
 */
val Configuration.isSystemInDarkTheme
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES


/**
 * Registers listener for configuration changes to retrieve whether system is in dark theme or not.
 * Immediately upon subscribing, it sends the current value and then registers listener for changes.
 */
fun ComponentActivity.isSystemInDarkTheme() = callbackFlow {
    channel.trySend(resources.configuration.isSystemInDarkTheme)

    val listener = Consumer<Configuration> {
        channel.trySend(it.isSystemInDarkTheme)
    }

    addOnConfigurationChangedListener(listener)

    awaitClose { removeOnConfigurationChangedListener(listener) }
}
    .distinctUntilChanged()
    .conflate()

@Composable
fun OnSVIcon(
    imageVector: ImageVector,
    contentDescription: String? = null
) = Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
