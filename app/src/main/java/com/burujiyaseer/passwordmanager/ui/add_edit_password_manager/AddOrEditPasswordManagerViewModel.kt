package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import com.burujiyaseer.passwordmanager.domain.usecase.delete_password_manager_entry.DeletePasswordManagerEntry
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.EncryptDecryptPassword
import com.burujiyaseer.passwordmanager.domain.usecase.get_website_fav_icon_url.SaveFavIconFromWebsiteUrl
import com.burujiyaseer.passwordmanager.domain.usecase.insert_password_manager.InsertPasswordManager
import com.burujiyaseer.passwordmanager.domain.usecase.read_password_manager_by_id.ReadPasswordManagerById
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel(assistedFactory = AddOrEditPasswordManagerViewModel.Factory::class)
class AddOrEditPasswordManagerViewModel @AssistedInject constructor(
    private val insertPasswordManager: InsertPasswordManager,
    private val encryptDecryptPassword: EncryptDecryptPassword,
    private val readPasswordManagerById: ReadPasswordManagerById,
    private val deletePasswordManagerEntry: DeletePasswordManagerEntry,
    private val saveFavIconFromWebsiteUrl: SaveFavIconFromWebsiteUrl,
    @Assisted private val entryId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun createKey(entryId: String?): AddOrEditPasswordManagerViewModel
    }

//    private val passwordEntryIdFlow = flow { emit(entryId) }
//        .stateIn(viewModelScope, SharingStarted.Lazily, entryId)
//        savedStateHandle.getStateFlow<String?>(PASSWORD_ENTRY_ID_KEY, null)

    /**
     * this flow represents the state of the password entry in the moment of entry,
     * when adding a new password -> null, when editing - the currently saved state of the
     * entry in the database.
     */
    val currentPasswordUIState = readPasswordManagerById(
        entryId
    ).map { passwordManagerModel ->
        entryId?.ifEmpty { null }.let { passwordEntryId ->
            utilLog("readPassword: $passwordManagerModel, entryId: $passwordEntryId")
            AddEditPasswordManagerAction.FillUIFields(
                uiPasswordModel =
                    passwordManagerModel?.run {
                        UIPasswordModel(
                            passwordManagerModel.entryId,
                            title = title,
                            account = account,
                            username = username,
                            password = password,
                            websiteUrl = websiteUrl,
                            description = description
                        )
                    } ?: UIPasswordModel(),
                titleResId = if (passwordEntryId != null) R.string.add else R.string.edit,
                addDeleteMenu = passwordEntryId != null
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddEditPasswordManagerAction.FillUIFields(UIPasswordModel(), R.string.add, false)
    )
    private val _uiActionFlow = MutableStateFlow<AddEditPasswordManagerAction?>(null)
    val uiActionFlow = _uiActionFlow.asStateFlow()

    private val _uiPasswordModelStateFlow = MutableStateFlow(UIPasswordModel())

    fun positiveDeletePasswordEntry() {
        viewModelScope.launch {
            //remove entry from database if necessary
            if (!entryId.isNullOrEmpty())
                deletePasswordManagerEntry(entryId)
            // return to view password fragment
            navigateBack()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiActionFlow.emit(
                AddEditPasswordManagerAction.NavigateBack
            )
        }
    }

    fun updateWebsite(websiteUrl: String) {
        _uiPasswordModelStateFlow.update { it.copy(websiteUrl = websiteUrl) }
    }

    fun updateTitle(title: String) {
        _uiPasswordModelStateFlow.update { it.copy(title = title) }
    }

    fun updatePassword(password: String) {
        _uiPasswordModelStateFlow.update { it.copy(password = password) }
    }

    fun updateAccount(account: String) {
        _uiPasswordModelStateFlow.update { it.copy(account = account) }
    }

    fun updateUsername(username: String) {
        _uiPasswordModelStateFlow.update { it.copy(username = username) }
    }

    fun updateDescription(description: String) {
        _uiPasswordModelStateFlow.update { it.copy(description = description) }
    }

    val shouldEnableDoneButton = _uiPasswordModelStateFlow.map {
        AddEditPasswordManagerAction.ToggleDoneBtnEnabled(
            listOf(it.title, it.password, it.websiteUrl).all(CharSequence::isNotBlank)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AddEditPasswordManagerAction.ToggleDoneBtnEnabled(false)
    )

    fun savePasswordEntry() {
        viewModelScope.launch {
            //show loading
            _uiActionFlow.emit(AddEditPasswordManagerAction.ShowLoader)
            val uiPasswordModel = _uiPasswordModelStateFlow.value
            // encrypt password and get password filename
            val deferredPasswordFilename = async {
                encryptDecryptPassword.encryptPassword(uiPasswordModel.password)
            }
            val deferredFavIconUrl = async {
                saveFavIconFromWebsiteUrl(uiPasswordModel.websiteUrl)
            }
            // await completion of all values
            val passwordFilename = deferredPasswordFilename.await()
            val favIconUrl = deferredFavIconUrl.await()
            utilLog(
                "uiPasswordModel: $uiPasswordModel \n" +
                        "password: ${encryptDecryptPassword.decryptPassword(passwordFilename)} \n" +
                        "favIconUrl: $favIconUrl"
            )
            // save the entry into the database
            insertPasswordManager(
                uiPasswordModel.run {
                    PasswordManagerModel(
                        // we use the current entryId, if null we generate a new one
                        entryId = uiPasswordModel.entryId
                            ?: UUID.randomUUID().toString(),
                        title = title,
                        account = account,
                        username = username,
                        password = EMPTY_STRING,
                        passwordFileName = passwordFilename,
                        websiteUrl = websiteUrl,
                        favIconUrl = favIconUrl,
                        description = description,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = 0L
                    )
                }
            )
            // navigate back to view password fragment
            navigateBack()
        }
    }

}