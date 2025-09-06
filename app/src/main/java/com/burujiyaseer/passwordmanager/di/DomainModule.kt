package com.burujiyaseer.passwordmanager.di

import com.burujiyaseer.passwordmanager.domain.usecase.check_password_strength.CheckPasswordStrength
import com.burujiyaseer.passwordmanager.domain.usecase.check_password_strength.DefaultCheckPasswordStrength
import com.burujiyaseer.passwordmanager.domain.usecase.clear_saved_password.ClearSavedMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.clear_saved_password.DefaultClearSavedMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.delete_password_manager_entry.DefaultDeletePasswordManagerEntry
import com.burujiyaseer.passwordmanager.domain.usecase.delete_password_manager_entry.DeletePasswordManagerEntry
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.DefaultEncryptDecryptPassword
import com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password.EncryptDecryptPassword
import com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries.DefaultGetAllPasswordManagerEntries
import com.burujiyaseer.passwordmanager.domain.usecase.get_all_password_manager_entries.GetAllPasswordManagerEntries
import com.burujiyaseer.passwordmanager.domain.usecase.get_password_manager_by_id.DefaultGetPasswordManagerById
import com.burujiyaseer.passwordmanager.domain.usecase.get_password_manager_by_id.GetPasswordManagerById
import com.burujiyaseer.passwordmanager.domain.usecase.get_website_fav_icon_url.DefaultSaveFavIconFromWebsiteUrl
import com.burujiyaseer.passwordmanager.domain.usecase.get_website_fav_icon_url.SaveFavIconFromWebsiteUrl
import com.burujiyaseer.passwordmanager.domain.usecase.insert_password_manager.DefaultInsertPasswordManager
import com.burujiyaseer.passwordmanager.domain.usecase.insert_password_manager.InsertPasswordManager
import com.burujiyaseer.passwordmanager.domain.usecase.read_ed_dialog.DefaultReadEdDialogState
import com.burujiyaseer.passwordmanager.domain.usecase.read_ed_dialog.ReadEdDialogState
import com.burujiyaseer.passwordmanager.domain.usecase.read_master_password.DefaultReadMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.read_master_password.ReadMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.read_password_manager_by_id.DefaultReadPasswordManagerById
import com.burujiyaseer.passwordmanager.domain.usecase.read_password_manager_by_id.ReadPasswordManagerById
import com.burujiyaseer.passwordmanager.domain.usecase.read_suggestions.DefaultReadSuggestions
import com.burujiyaseer.passwordmanager.domain.usecase.read_suggestions.ReadSuggestions
import com.burujiyaseer.passwordmanager.domain.usecase.save_master_password.DefaultSaveMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.save_master_password.SaveMasterPassword
import com.burujiyaseer.passwordmanager.domain.usecase.save_suggestion.DefaultSaveSuggestion
import com.burujiyaseer.passwordmanager.domain.usecase.save_suggestion.SaveSuggestion
import com.burujiyaseer.passwordmanager.domain.usecase.show_ed_dialog.DefaultSaveShownEdDialog
import com.burujiyaseer.passwordmanager.domain.usecase.show_ed_dialog.SaveShownEdDialog
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Binds
    fun bindGetPasswordManagerById(useCase: DefaultGetPasswordManagerById): GetPasswordManagerById

    @Binds
    fun bindReadPasswordManagerById(useCase: DefaultReadPasswordManagerById): ReadPasswordManagerById

    @Binds
    fun insertPasswordManager(useCase: DefaultInsertPasswordManager): InsertPasswordManager

    @Binds
    fun bindDeletePasswordManager(useCase: DefaultDeletePasswordManagerEntry): DeletePasswordManagerEntry

    @Binds
    fun bindGetAllPasswordManagerEntries(useCase: DefaultGetAllPasswordManagerEntries): GetAllPasswordManagerEntries

    @Binds
    fun bindEncryptDecryptPassword(useCase: DefaultEncryptDecryptPassword): EncryptDecryptPassword

    @Binds
    fun bindReadMasterPassword(useCase: DefaultReadMasterPassword): ReadMasterPassword

    @Binds
    fun bindSaveMasterPassword(useCase: DefaultSaveMasterPassword): SaveMasterPassword

    @Binds
    fun bindClearSavedMasterPassword(useCase: DefaultClearSavedMasterPassword): ClearSavedMasterPassword

    @Binds
    fun bindCheckPasswordStrength(useCase: DefaultCheckPasswordStrength): CheckPasswordStrength

    @Binds
    fun bindSaveFavIconWebsiteUrl(useCase: DefaultSaveFavIconFromWebsiteUrl): SaveFavIconFromWebsiteUrl

    @Binds
    fun bindReadEdDialogState(useCase: DefaultReadEdDialogState): ReadEdDialogState

    @Binds
    fun bindSaveEdDialogState(useCase: DefaultSaveShownEdDialog): SaveShownEdDialog

    @Binds
    fun bindSaveSuggestion(useCase: DefaultSaveSuggestion): SaveSuggestion

    @Binds
    fun bindReadSuggestions(useCase: DefaultReadSuggestions): ReadSuggestions
}