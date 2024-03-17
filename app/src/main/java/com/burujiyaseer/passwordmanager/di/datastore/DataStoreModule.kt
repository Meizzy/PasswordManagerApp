package com.burujiyaseer.passwordmanager.di.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.burujiyaseer.passwordmanager.data.local.datastore.DefaultPreferencesDataSource
import com.burujiyaseer.passwordmanager.data.local.datastore.PreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {


    @Provides
    fun provideDatastore(@ApplicationContext appContext: Context): DataStore<Preferences> = appContext.dataStore

    @Provides
    @Singleton
    fun providePreferencesDataSource(
        defaultPreferencesDataSource: DefaultPreferencesDataSource
    ): PreferencesDataSource = defaultPreferencesDataSource
}