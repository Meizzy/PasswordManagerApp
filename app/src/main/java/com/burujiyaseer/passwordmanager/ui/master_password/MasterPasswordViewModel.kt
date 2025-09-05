package com.burujiyaseer.passwordmanager.ui.master_password

import android.app.KeyguardManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength
import com.burujiyaseer.passwordmanager.domain.usecase.check_password_strength.CheckPasswordStrength
import com.burujiyaseer.passwordmanager.domain.usecase.clear_saved_password.ClearSavedMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.EncryptDecryptPassword
import com.burujiyaseer.passwordmanager.domain.usecase.read_ed_dialog.ReadEdDialogState
import com.burujiyaseer.passwordmanager.domain.usecase.read_master_password.ReadMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.save_master_password.SaveMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.show_ed_dialog.SaveShownEdDialog
import com.burujiyaseer.passwordmanager.ui.util.Constants
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DELAY_MS = 500L

@HiltViewModel()
class MasterPasswordViewModel @Inject constructor(
    readMasterPassword: ReadMasterPassword,
    readEdDialogState: ReadEdDialogState,
    private val encryptDecryptPassword: EncryptDecryptPassword,
    private val checkPasswordStrength: CheckPasswordStrength,
    private val saveMasterPassword: SaveMasterPassword,
    private val clearSavedMasterPassword: ClearSavedMasterPassword,
    private val saveShownEdDialog: SaveShownEdDialog,
    @ApplicationContext context: Context
) : ViewModel() {

    private val inputMasterPassword = MutableStateFlow(Constants.EMPTY_STRING)
    private val biometricsSignInFlow = MutableStateFlow(
        context.getSystemService<KeyguardManager>()?.isDeviceSecure == true
    )

    fun doAfterTextChanged(inputText: String) {
        inputMasterPassword.update { inputText }
    }

    private val masterPasswordFlow = readMasterPassword()
    val hasShownEdDialog = readEdDialogState()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            true
        )

    @OptIn(FlowPreview::class)
    val masterPasswordUIState = combine(
        //in order to reduce the frequency of running password checks,
        //we delay by half a second
        inputMasterPassword.debounce(DELAY_MS),
        masterPasswordFlow,
//        biometricsSignInFlow
    ) { inputText, masterPassword ->
        if (masterPassword.isNullOrEmpty()) {
            val passwordStrength = checkPasswordStrength(inputText)
            utilLog("firstTime: passwordStrength: $passwordStrength")
            MasterPasswordUIAction.PasswordStrengthResult(passwordStrength)
        } else {
            if (!hasShownEdDialog.value) {
                //first time login
                saveShownEdDialog()
            }
            val shouldUseBiometrics = biometricsSignInFlow.value
            if (shouldUseBiometrics) MasterPasswordUIAction.ShowBiometricsDialog
            else {
                val decryptedPassword = encryptDecryptPassword.decryptPassword(masterPassword)
                utilLog("alreadySignedIn: decryptedPassword: $decryptedPassword")
                MasterPasswordUIAction.ValidatePassword(inputText == decryptedPassword)
            }
        }
//    }.distinctUntilChanged { old, new ->
//        utilLog("distinctUntilChanged: old: $old, new: $new")
//        old is MasterPasswordUIAction.ValidatePassword && new is MasterPasswordUIAction.ValidatePassword
//                && old.isCorrect == new.isCorrect
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MasterPasswordUIAction.ValidatePassword(false)
    )

    fun registerMasterPassword() {
        viewModelScope.launch {
            val currentMasterPassword = inputMasterPassword.value
            (masterPasswordUIState.value as? MasterPasswordUIAction.PasswordStrengthResult)?.let { passwordStrengthResult ->
                when (passwordStrengthResult.passwordStrength) {
                    PasswordStrength.OKAY, PasswordStrength.STRONG -> {
                        val encryptedPassword =
                            encryptDecryptPassword.encryptPassword(currentMasterPassword)
                        utilLog("registerMasterPassword: encryptedPassword: $encryptedPassword")
                        saveMasterPassword(encryptedPassword)
                    }

                    else -> Unit
                }
            }
        }
    }

    fun onBiometricUnsuccessful() {
        viewModelScope.launch {
            biometricsSignInFlow.update { false }
            doAfterTextChanged("")
        }
    }

    private var retryCount = 0
    fun onRetryBiometric() {
        if (retryCount <= 5) viewModelScope.launch {
            biometricsSignInFlow.update { true }
            doAfterTextChanged("")
            retryCount++
        }
    }

    fun onBiometricSuccess() {
        viewModelScope.launch {
            biometricsSignInFlow.update { false }
            val masterPassword = masterPasswordFlow.firstOrNull() ?: return@launch
            val decryptedPassword =
                encryptDecryptPassword.decryptPassword(masterPassword) ?: return@launch
            if (decryptedPassword == inputMasterPassword.value) doAfterTextChanged("")
            doAfterTextChanged(decryptedPassword)
        }
    }

    fun clearSavedPassword() {
        viewModelScope.launch {
            clearSavedMasterPassword()
        }
    }

}