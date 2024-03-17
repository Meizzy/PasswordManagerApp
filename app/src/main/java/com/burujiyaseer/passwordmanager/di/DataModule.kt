package com.burujiyaseer.passwordmanager.di

import android.app.Application
import androidx.room.Room
import com.burujiyaseer.passwordmanager.data.local.PasswordManagerDatabase
import com.burujiyaseer.passwordmanager.data.repository.DefaultPasswordManagerRepository
import com.burujiyaseer.passwordmanager.data.util.DatabaseConstants.DATABASE_NAME
import com.burujiyaseer.passwordmanager.di.coroutines.CoroutineDispatcherProvider
import com.burujiyaseer.passwordmanager.domain.repository.PasswordManagerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePasswordManagerDatabase(app: Application): PasswordManagerDatabase {
        return Room.databaseBuilder(
            app,
            PasswordManagerDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePasswordManagerRepository(db: PasswordManagerDatabase, coroutineDispatcherProvider: CoroutineDispatcherProvider): PasswordManagerRepository {
        return DefaultPasswordManagerRepository(db.passwordManagerDao, coroutineDispatcherProvider)
    }

}