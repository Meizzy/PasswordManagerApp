package com.burujiyaseer.passwordmanager.ui.master_password

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.ui.util.DefaultLambda
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricAuthHelper
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricResult
import com.burujiyaseer.passwordmanager.ui.util.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    modifier: Modifier,
    viewModel: MasterPasswordViewModel = hiltViewModel(),
    biometricAuthHelper: BiometricAuthHelper,
    goToNextScreen: DefaultLambda
) {
    val hasShownEdDialog by viewModel.hasShownEdDialog.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as FragmentActivity
    val value: MasterPasswordUIAction by viewModel.masterPasswordUIState.collectAsStateWithLifecycle()

    Column(
        modifier
            .padding(16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!hasShownEdDialog) {
            EducationalAuthDialog {  }
        }
        var showButtonText by remember { mutableStateOf<Boolean?>(null) }
        var helperText by remember {
            mutableIntStateOf(R.string.required_helper_text)
        }
        val colorScheme = MaterialTheme.colorScheme
        var helperTextColor by remember {
            mutableStateOf(colorScheme.primary)
        }
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = stringResource(id = R.string.welcome_title),
            modifier = Modifier.padding(32.dp),
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Box {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp),
//                    .animateContentSize()
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Log.d("LoginScreen", "value def: $value")
        when (value) {
            is MasterPasswordUIAction.PasswordStrengthResult -> {
                val passwordStrengthResult = value as MasterPasswordUIAction.PasswordStrengthResult
                showButtonText = if (passwordStrengthResult.isWeak) null else true
                helperText = passwordStrengthResult.helperTextRes
                helperTextColor = colorResource(passwordStrengthResult.helperTextColor)
            }

            is MasterPasswordUIAction.ValidatePassword -> {
                val isCorrect = (value as MasterPasswordUIAction.ValidatePassword).isCorrect
                showButtonText = isCorrect
                if (isCorrect) {
//                    signInAnimAndNavigate()
                    Snackbar { Text("Login successful") }
                    LaunchedEffect(true) {
                        goToNextScreen()
                    }
                }
            }

            MasterPasswordUIAction.ShowBiometricsDialog -> {
                ProcessBiometricsResult(value, biometricAuthHelper, activity, viewModel)
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        var passwordText by rememberSaveable {
            mutableStateOf("")
        }
        OutlinedTextField(
            modifier = Modifier.padding(16.dp),
            value = passwordText,
            onValueChange = { currentText ->
                if (passwordText != currentText) {
                    passwordText = currentText
                    viewModel.doAfterTextChanged(currentText)
                }
            },
            label = {
                Text(
                    text = stringResource(id = R.string.master_password_hint)
                )
            },
            supportingText = {
                Text(
                    text = stringResource(id = helperText),
                    color = helperTextColor
                )
            },
            isError = helperTextColor == Color.Red,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = helperTextColor,
                unfocusedBorderColor = helperTextColor,
                focusedLabelColor = helperTextColor,
                unfocusedLabelColor = helperTextColor,
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(
            modifier = Modifier.height(64.dp)
        )
        when (showButtonText) {
            true -> Button(
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .padding(16.dp),
                onClick = {
                    viewModel.registerMasterPassword()
                }
            ) {
                Text(text = stringResource(id = R.string.save))
            }

            false -> Button(
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .padding(16.dp),
                onClick = {
                    viewModel.clearSavedPassword()
                }
            ) {
                Text(text = stringResource(id = android.R.string.cancel))
            }

            null -> Unit
        }

    }
}

@Composable
private fun ProcessBiometricsResult(
    value: MasterPasswordUIAction,
    biometricAuthHelper: BiometricAuthHelper,
    activity: FragmentActivity,
    viewModel: MasterPasswordViewModel
) {
    LaunchedEffect(value) {
        biometricAuthHelper.authenticate(activity)
            .collectLatest { biometricResult ->
                when (biometricResult) {
                    BiometricResult.CanceledBySystem,
                    BiometricResult.CanceledByUser,
                    BiometricResult.HardwareUnavailableOrDisabled,
                    is BiometricResult.Failure -> {
                        viewModel.onBiometricUnsuccessful()
                    }

                    BiometricResult.Retry -> viewModel.onRetryBiometric()

                    is BiometricResult.Success -> {
                        viewModel.onBiometricSuccess()
                    }

                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalAuthDialog(modifier: Modifier = Modifier, onDismiss: DefaultLambda) {
    var shouldOpen by remember { mutableStateOf(true) }
    if (shouldOpen) {
        BasicAlertDialog(
            onDismissRequest = {
//                shouldOpen = false
//                onDismiss()
            },
        ) {
            Surface(
                modifier = modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.master_password_hint),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.first_time_dialog_message)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            shouldOpen = false
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(android.R.string.ok)
                        )
                    }
                }
            }
        }
    }
}