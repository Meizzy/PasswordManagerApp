package com.burujiyaseer.passwordmanager.ui.util

import com.burujiyaseer.passwordmanager.ui.ui.theme.LocalAppLifeCycle
import android.content.res.Configuration
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.coroutines.EmptyCoroutineContext

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

@Composable
@Suppress("StateFlowValueCalledInComposition")
fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> {
    val lifecycle = LocalAppLifeCycle.current
    LaunchedEffect(true) {
        lifecycle.currentStateFlow.collect {
            utilLog("extension lifecycle: ${it.name}, atLeastStarted: ${it.isAtLeast(Lifecycle.State.STARTED)}")
        }
    }
    return collectAsStateWithLifecycle(
        initialValue = this.value,
        lifecycle = lifecycle,
        minActiveState = Lifecycle.State.STARTED,
        context = EmptyCoroutineContext,
    )
}
private const val STOP_SUBSCRIPTION_TIMEOUT = 5_000L

/**
 * the most common use case of [SharingStarted.WhileSubscribed]
 * with a timeout of [STOP_SUBSCRIPTION_TIMEOUT]
 */
fun SharingStarted.Companion.StandardWhileSubscribed() = WhileSubscribed(STOP_SUBSCRIPTION_TIMEOUT)