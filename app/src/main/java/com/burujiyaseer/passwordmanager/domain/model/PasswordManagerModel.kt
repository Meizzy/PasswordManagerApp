package com.burujiyaseer.passwordmanager.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.ACCOUNT
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.CREATED_AT
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.DATABASE_NAME
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.DESCRIPTION
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.ENTRY_ID
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.FAV_ICON_URL
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.PASSWORD
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.PASSWORD_FILE_NAME
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.TITLE
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.UPDATED_AT
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.USERNAME
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.WEBSITE_URL

@Entity(tableName = DATABASE_NAME)
data class PasswordManagerModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ENTRY_ID)
    val entryId: String,
    @ColumnInfo(name = TITLE)
    val title: String,
    @ColumnInfo(name = ACCOUNT)
    val account: String,
    @ColumnInfo(name = USERNAME)
    val username: String,
    @ColumnInfo(name = WEBSITE_URL)
    val websiteUrl: String,
    @ColumnInfo(name = FAV_ICON_URL)
    val favIconUrl: String?,
    @ColumnInfo(name = PASSWORD)
    val password: String,
    @ColumnInfo(name = PASSWORD_FILE_NAME)
    val passwordFileName: String,
    @ColumnInfo(name = DESCRIPTION)
    val description: String,
    @ColumnInfo(name = CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = UPDATED_AT)
    val updatedAt: Long
)
