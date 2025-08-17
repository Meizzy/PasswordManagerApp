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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
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

    private val passwordEntryIdFlow = flow { emit(entryId) }
        .stateIn(viewModelScope, SharingStarted.Lazily, entryId)
//        savedStateHandle.getStateFlow<String?>(PASSWORD_ENTRY_ID_KEY, null)

    /**
     * this flow represents the state of the password entry in the moment of entry,
     * when adding a new password -> null, when editing - the currently saved state of the
     * entry in the database.
     */
    val currentPasswordUIState = passwordEntryIdFlow.map { passwordEntryId ->
        AddEditPasswordManagerAction.FillUIFields(
            uiPasswordModel = passwordEntryId?.ifEmpty { null }?.let {
                readPasswordManagerById(
                    passwordEntryId
                )?.let { passwordManagerModel ->
                    utilLog("readPassword: $passwordManagerModel, entryId: $entryId, flowEntryId: $passwordEntryId")
                    passwordManagerModel.run {
                        UIPasswordModel(
                            passwordManagerModel.entryId,
                            title = title,
                            account = account,
                            username = username,
                            password = password,
                            websiteUrl = websiteUrl,
                            description = description
                        )
                    }
                }
            } ?: UIPasswordModel(),
            titleResId = if (passwordEntryId.isNullOrEmpty()) R.string.add else R.string.edit,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        AddEditPasswordManagerAction.FillUIFields(UIPasswordModel(), R.string.add)
    )

    private val _titleStateFlow = MutableStateFlow(EMPTY_STRING)
    private val _passwordStateFlow = MutableStateFlow(EMPTY_STRING)
    private val _websiteStateFlow = MutableStateFlow(EMPTY_STRING)
    private val _uiActionFlow = MutableStateFlow<AddEditPasswordManagerAction?>(null)
    val uiActionFlow = _uiActionFlow.asStateFlow()

    fun onEvent(event: AddEditPasswordManagerEvent) {
        when (event) {
            is AddEditPasswordManagerEvent.AddedPassword -> updatePassword(event.password)
            is AddEditPasswordManagerEvent.AddedTitle -> updateTitle(event.title)
            is AddEditPasswordManagerEvent.AddedWebsite -> updateWebsite(event.websiteUrl)
            is AddEditPasswordManagerEvent.SavePasswordEntry -> savePasswordEntry(event.uiPasswordModel)
            AddEditPasswordManagerEvent.DeletePasswordEntryClicked -> deletePasswordEntryClicked()
            AddEditPasswordManagerEvent.PositiveDeletePasswordEntry -> positiveDeletePasswordEntry()
            AddEditPasswordManagerEvent.LeavePasswordEntryClicked -> leavePasswordEntryClicked()
            AddEditPasswordManagerEvent.PositiveLeavePasswordEntry -> navigateBack()
            AddEditPasswordManagerEvent.NegativeLeavePasswordEntry -> dismissDialog()
        }
    }

    private fun dismissDialog() {
        viewModelScope.launch {
            _uiActionFlow.emit(
                AddEditPasswordManagerAction.DismissDialog
            )
        }
    }

    private fun leavePasswordEntryClicked() {
        viewModelScope.launch {
            _uiActionFlow.emit(
                AddEditPasswordManagerAction.ShowConfirmExitDialog
            )
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiActionFlow.emit(
                AddEditPasswordManagerAction.NavigateBack
            )
        }
    }

    private fun positiveDeletePasswordEntry() {
        viewModelScope.launch {
            val passwordEntryId = passwordEntryIdFlow.value
            //remove entry from database if necessary
            if (!passwordEntryId.isNullOrEmpty())
                deletePasswordManagerEntry(passwordEntryId)
            // return to view password fragment
            navigateBack()
        }
    }

    private fun deletePasswordEntryClicked() {
        viewModelScope.launch {
            _uiActionFlow.emit(
                AddEditPasswordManagerAction.ShowConfirmDeleteDialog
            )
        }
    }


    private fun updateWebsite(websiteUrl: String) {
        _websiteStateFlow.update { websiteUrl }
    }

    private fun updateTitle(title: String) {
        _titleStateFlow.update { title }
    }

    private fun updatePassword(password: String) {
        _passwordStateFlow.update { password }
    }

    val shouldEnableDoneButton = combine(
        _titleStateFlow,
        _passwordStateFlow,
        _websiteStateFlow
    ) { title, password, websiteUrl ->
        AddEditPasswordManagerAction.ToggleDoneBtnEnabled(
            listOf(title, password, websiteUrl).all { it.isNotBlank() }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AddEditPasswordManagerAction.ToggleDoneBtnEnabled(false)
    )

    private fun savePasswordEntry(uiPasswordModel: UIPasswordModel) {
        viewModelScope.launch {
            //show loading
            _uiActionFlow.emit(AddEditPasswordManagerAction.ShowLoader)
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
                        entryId = currentPasswordUIState.value.uiPasswordModel.entryId
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