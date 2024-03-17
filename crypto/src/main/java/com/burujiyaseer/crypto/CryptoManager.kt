package com.burujiyaseer.crypto

import java.io.InputStream
import java.io.OutputStream

interface CryptoManager {
    suspend fun encrypt(bytes: ByteArray, outputStream: OutputStream)
    fun decrypt(inputStream: InputStream): ByteArray
}