package com.burujiyaseer.passwordmanager.di.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatcherProvider {

    fun main(): CoroutineDispatcher = Dispatchers.Main

    fun mainImmediate(): CoroutineDispatcher = Dispatchers.Main.immediate

    fun default(): CoroutineDispatcher = Dispatchers.Default

    fun io(): CoroutineDispatcher = Dispatchers.IO

    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}