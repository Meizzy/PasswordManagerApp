package com.burujiyaseer.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class DefaultCryptoManager() : CryptoManager {

    private val keyStore = KeyStore.getInstance(KEYSTORE_TYPE).apply { load(null) }

    private fun newEncryptCipher(): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }

    private fun newDecryptCipher(iv: ByteArray): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }

    private fun getKey(): SecretKey {
        val existing = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existing?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setKeySize(128) // AES-128; adjust if you really need 256 and device supports it
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    override suspend fun encrypt(bytes: ByteArray, outputStream: OutputStream) {
        val cipher = newEncryptCipher()
        val iv = cipher.iv // always block size (16 for AES/CBC)
        DataOutputStream(BufferedOutputStream(outputStream)).use { out ->
            // write IV length (int) + IV
            out.writeInt(iv.size)
            out.write(iv)

            val inStream = ByteArrayInputStream(bytes)
            val buffer = ByteArray(CHUNK_SIZE)

            var n = inStream.read(buffer)
            while (n != -1) {
                val chunk = cipher.update(buffer, 0, n)
                if (chunk != null && chunk.isNotEmpty()) out.write(chunk)
                n = inStream.read(buffer)
            }

            val last = cipher.doFinal()
            if (last.isNotEmpty()) out.write(last)
        }
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        DataInputStream(BufferedInputStream(inputStream)).use { inp ->
            val ivSize = inp.readInt()
            require(ivSize in 12..32) { "Invalid IV size prefix: $ivSize" } // sanity check
            val iv = ByteArray(ivSize)
            inp.readFully(iv) // ensure IV is fully read

            val cipher = newDecryptCipher(iv)
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(CHUNK_SIZE)

            var n = inp.read(buffer)
            while (n != -1) {
                val chunk = cipher.update(buffer, 0, n)
                if (chunk != null && chunk.isNotEmpty()) out.write(chunk)
                n = inp.read(buffer)
            }

            val last = cipher.doFinal()
            if (last.isNotEmpty()) out.write(last)

            return out.toByteArray()
        }
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEYSTORE_TYPE = "AndroidKeyStore"
        private const val CHUNK_SIZE = 4 * 1024
        private const val ALIAS = "my_alias"
    }
}
