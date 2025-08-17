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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricAuthHelper
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricResult
import com.burujiyaseer.passwordmanager.ui.util.utilLog

@Composable
fun LoginScreen(
    modifier: Modifier,
    viewModel: MasterPasswordViewModel,
    biometricAuthHelper: BiometricAuthHelper,
    goToNextScreen: () -> Unit,
) {
    val activity = LocalActivity.current as FragmentActivity
    Column(
        modifier
            .padding(16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val value: MasterPasswordUIAction by viewModel.masterPasswordUIState.collectAsStateWithLifecycle()
        var showButtonText: Boolean? = null
        var passwordText by rememberSaveable {
            mutableStateOf("")
        }
        var helperText by remember {
            mutableIntStateOf(R.string.required_helper_text)
        }
        val colorScheme = MaterialTheme.colorScheme
        var helperTextColor by remember {
            mutableStateOf(colorScheme.primary)
        }

        LaunchedEffect(true) {
            biometricAuthHelper.authenticate(activity)
                .also { biometricResult ->
                    utilLog("authenticate: result: $biometricResult")
                    when (biometricResult) {
                        BiometricResult.CanceledBySystem,
                        BiometricResult.CanceledByUser,
                        BiometricResult.HardwareUnavailableOrDisabled -> Unit

                        is BiometricResult.Failure -> {
//                            stringResource(
//                                R.string.biometric_error,
//                                biometricResult.message
//                            )
                        }

                        BiometricResult.Retry -> biometricAuthHelper.authenticate(activity)
                        is BiometricResult.Success -> goToNextScreen()
                    }
                    Unit
                }
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
                showButtonText =
                    if ((value as MasterPasswordUIAction.PasswordStrengthResult).passwordStrength == PasswordStrength.WEAK) null else true
                when ((value as MasterPasswordUIAction.PasswordStrengthResult).passwordStrength) {
                    PasswordStrength.WEAK -> {
                        helperText = R.string.weak_helper_text
                        helperTextColor = Color.Red
                    }

                    PasswordStrength.OKAY -> {
                        helperTextColor = colorResource(R.color.md_yellow_900)
                        helperText = R.string.okay_helper_text
                    }

                    PasswordStrength.STRONG -> {
                        val greenColor = colorResource(R.color.md_green_900)
                        helperTextColor = greenColor
                        helperText = R.string.strong_helper_text
                    }
                }
            }

            is MasterPasswordUIAction.ValidatePassword -> {
                val isCorrect = (value as MasterPasswordUIAction.ValidatePassword).isCorrect
                showButtonText = isCorrect
                if (isCorrect) {
//                    signInAnimAndNavigate()
//                    Snackbar { Text("Login successful") }
                    LaunchedEffect(true) {
                        goToNextScreen()
                    }
                }
//                }
            }

            MasterPasswordUIAction.IsFirstTime -> EducationalAuthDialog()
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalAuthDialog(modifier: Modifier = Modifier) {
    var shouldOpen by remember { mutableStateOf(true) }
    if (shouldOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                shouldOpen = false
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
                        onClick = { shouldOpen = false },
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