package com.burujiyaseer.passwordmanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.DATABASE_NAME
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordManagerDao {

    @Query("SELECT * FROM $DATABASE_NAME")
    fun getAllPasswordEntries() : Flow<List<PasswordManagerModel>>

    @Query("SELECT * FROM $DATABASE_NAME WHERE $DATABASE_NAME.entry_id = :entryId")
    suspend fun getPasswordEntryById(entryId: String): PasswordManagerModel?

    @Query("SELECT * FROM $DATABASE_NAME WHERE $DATABASE_NAME.entry_id = :entryId")
    fun readPasswordEntryById(entryId: String): Flow<PasswordManagerModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasswordManagerModel(passwordManagerModel: PasswordManagerModel)

    @Query("DELETE FROM $DATABASE_NAME WHERE $DATABASE_NAME.entry_id = :entryId")
    suspend fun deletePasswordManagerModelById(entryId: String)
}