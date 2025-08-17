package com.burujiyaseer.passwordmanager.ui.master_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength
import com.burujiyaseer.passwordmanager.domain.usecase.check_password_strength.CheckPasswordStrength
import com.burujiyaseer.passwordmanager.domain.usecase.clear_saved_password.ClearSavedMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.EncryptDecryptPassword
import com.burujiyaseer.passwordmanager.domain.usecase.read_master_password.ReadMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.save_master_password.SaveMasterPassword
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DELAY_MS = 500L

@HiltViewModel()
class MasterPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    readMasterPassword: ReadMasterPassword,
    private val encryptDecryptPassword: EncryptDecryptPassword,
    private val checkPasswordStrength: CheckPasswordStrength,
    private val saveMasterPassword: SaveMasterPassword,
    private val clearSavedMasterPassword: ClearSavedMasterPassword
) : ViewModel() {

    interface Factory {
        fun create()
    }

    private val inputMasterPassword = MutableStateFlow<String?>(null)

    fun doAfterTextChanged(inputText: String) {
        inputMasterPassword.update { inputText }
    }

    private val masterPasswordFlow = readMasterPassword()

    val hasNotPreviouslySignedIn = masterPasswordFlow.map { true }.distinctUntilChanged().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )

    @OptIn(FlowPreview::class)
    val masterPasswordUIState = combine(
        //in order to reduce the frequency of running password check,
        //we delay by half a second
        inputMasterPassword.debounce(DELAY_MS),
        masterPasswordFlow
    ) { inputText, masterPassword ->
        //check if user has previously signed in
        if (masterPassword.isNullOrEmpty()) {
            if (inputText == null) MasterPasswordUIAction.IsFirstTime
            else {
                val passwordStrength = checkPasswordStrength(inputText)
                utilLog("firstTime: passwordStrength: $passwordStrength")
                MasterPasswordUIAction.PasswordStrengthResult(passwordStrength)
            }
        } else {
            val decryptedPassword = encryptDecryptPassword.decryptPassword(masterPassword)
            utilLog("alreadySignedIn: decryptedPassword: $decryptedPassword")
            MasterPasswordUIAction.ValidatePassword(inputText == decryptedPassword)
        }
    }.distinctUntilChanged { old, new ->
        utilLog("distinctUntilChanged: old: $old, new: $new")
        old is MasterPasswordUIAction.ValidatePassword && new is MasterPasswordUIAction.ValidatePassword
                && old.isCorrect == new.isCorrect
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MasterPasswordUIAction.ValidatePassword(false)
    )

    fun registerMasterPassword() {
        viewModelScope.launch {
            val currentMasterPassword = inputMasterPassword.value ?: return@launch
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

    fun clearSavedPassword() {
        viewModelScope.launch {
            clearSavedMasterPassword()
        }
    }

}