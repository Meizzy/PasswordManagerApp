package com.burujiyaseer.passwordmanager.di.coroutines

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DispatcherModule {
    @Binds
    fun bindDispatcherProvider(useCase: DefaultCoroutineDispatcherProvider): CoroutineDispatcherProvider
}