package com.burujiyaseer.passwordmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.DATABASE_VERSION
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

@Database(
    entities = [PasswordManagerModel::class],
    version = DATABASE_VERSION
)
abstract class PasswordManagerDatabase : RoomDatabase() {
    abstract val passwordManagerDao: PasswordManagerDao

}