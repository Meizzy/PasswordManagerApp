package com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password

import android.app.KeyguardManager
import android.content.Context
import androidx.core.content.getSystemService
import com.burujiyaseer.crypto.CryptoManager
import com.burujiyaseer.crypto.DefaultCryptoManager
import com.burujiyaseer.passwordmanager.di.coroutines.CoroutineDispatcherProvider
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
private const val EMPTY_STRING = ""
@Suppress("BlockingMethodInNonBlockingContext")
class DefaultEncryptDecryptPassword @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : EncryptDecryptPassword {

    private val cryptoManager: CryptoManager by lazy {
        DefaultCryptoManager(
            appContext.getSystemService<KeyguardManager>()?.isDeviceSecure == true
        )
    }

    override suspend fun encryptPassword(password: String, fileName: String): String = kotlin.runCatching {
        withContext(dispatcherProvider.io()) {
            val bytes = password.encodeToByteArray()
            val file = File(appContext.filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }

            //encrypt the data
            cryptoManager.encrypt(
                bytes = bytes,
                outputStream = FileOutputStream(file)
            )
            fileName
        }
    }.onFailure { utilLog("encrypt password error: $it, message:${it.message}") }.getOrDefault(EMPTY_STRING)

    override suspend fun decryptPassword(fileName: String): String? = kotlin.runCatching {
        withContext(dispatcherProvider.io()) {
            val file = File(appContext.filesDir, fileName)

            //decrypt the contents of the file
            cryptoManager.decrypt(
                inputStream = FileInputStream(file)
            ).decodeToString()
        }
    }.onFailure { utilLog("decrypt password error: $it, message:${it.message}") }.getOrNull()
}