package com.burujiyaseer.passwordmanager.ui.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.burujiyaseer.passwordmanager.ui.add_edit_password_manager.AddOrEditPasswordManagerViewModel
import com.burujiyaseer.passwordmanager.ui.add_edit_password_manager.AddOrEditPasswordScreen
import com.burujiyaseer.passwordmanager.ui.master_password.LoginScreen
import com.burujiyaseer.passwordmanager.ui.master_password.MasterPasswordViewModel
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricAuthHelperImpl
import com.burujiyaseer.passwordmanager.ui.view_password_manager.ViewPasswordsScreen
import com.burujiyaseer.passwordmanager.ui.view_password_manager.ViewPasswordsViewModel
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(
        Destination.LoginDestination
    )
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        modifier = modifier
    ) { key ->
        when (key) {
            Destination.LoginDestination -> {
                NavEntry(key) {
                    val viewModel = hiltViewModel<MasterPasswordViewModel>()
                    LoginScreen(
                        modifier = modifier,
                        viewModel = viewModel,
                        biometricAuthHelper = BiometricAuthHelperImpl(),
                        goToNextScreen = {
                            backStack.remove(key)
                            backStack.add(Destination.ViewPasswordDestination)
                        }
                    )
                }
            }

            Destination.ViewPasswordDestination -> {
                NavEntry(key) {
                    val viewModel = hiltViewModel<ViewPasswordsViewModel>()
                    ViewPasswordsScreen(
                        modifier = modifier,
                        onSearchClick = {

                        },
                        onAddFabClick = { entryId ->
                            backStack.add(Destination.AddOrEditPasswordDestination(entryId))
                        },
                        value = viewModel.passwordEntriesUIState.collectAsStateWithLifecycle().value,
                    )
                }
            }

            is Destination.AddOrEditPasswordDestination -> {
                NavEntry(key) {
                    val viewModel =
                        hiltViewModel<AddOrEditPasswordManagerViewModel, AddOrEditPasswordManagerViewModel.Factory> {
                            it.createKey(key.entryId)
                        }
                    AddOrEditPasswordScreen(
                        modifier = modifier,
                        viewModel = viewModel
                    ) {
                        backStack.remove(key)
                    }
                }
            }

            else -> throw IllegalArgumentException("unknown key $key")
        }
    }
}

sealed interface Destination : NavKey {
    @Serializable
    data object LoginDestination : Destination

    @Serializable
    data object ViewPasswordDestination : Destination

    @Serializable
    data class AddOrEditPasswordDestination(val entryId: String?) : Destination
}