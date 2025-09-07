package com.burujiyaseer.passwordmanager.ui

import com.burujiyaseer.passwordmanager.ui.ui.theme.LocalAppLifeCycle
import com.burujiyaseer.passwordmanager.ui.ui.theme.LocalBackgroundTheme
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.burujiyaseer.passwordmanager.ui.ui.navigation.NavigationRoot
import com.burujiyaseer.passwordmanager.ui.ui.theme.PasswordManagerTheme
import com.burujiyaseer.passwordmanager.ui.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComposeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        var darkTheme by mutableStateOf(resources.configuration.isSystemInDarkTheme)

        // Update the theme
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                isSystemInDarkTheme().onEach { darkTheme = it }
                    .collect { darkTheme ->
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                Color.TRANSPARENT,
                                Color.TRANSPARENT
                            ) {
                                darkTheme
                            },
                            navigationBarStyle = SystemBarStyle.auto(lightScrim, darkScrim) {
                                darkTheme
                            }
                        )

                    }
            }
        }

        setContent {
            PasswordManagerTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                // A surface container using the 'background' color from the theme
                AppBackground(composeActivity = this, content = {
                    NavigationRoot()
                })
            }
        }
    }
}

/**
 * The main background for the app.
 * Uses [LocalBackgroundTheme] to set the color and tonal elevation of a [Surface].
 *
 * @param modifier Modifier to be applied to the background.
 * @param content The background content.
 */
@Composable
private fun AppBackground(
    modifier: Modifier = Modifier,
    composeActivity: ComposeActivity,
    content: @Composable () -> Unit,
) {
    val color = LocalBackgroundTheme.current.color
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation
    Surface(
        color = if (color == androidx.compose.ui.graphics.Color.Unspecified) androidx.compose.ui.graphics.Color.Transparent else color,
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation,
        modifier = modifier.fillMaxSize(),
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp, LocalAppLifeCycle provides composeActivity.lifecycle) {
            content()
        }
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)