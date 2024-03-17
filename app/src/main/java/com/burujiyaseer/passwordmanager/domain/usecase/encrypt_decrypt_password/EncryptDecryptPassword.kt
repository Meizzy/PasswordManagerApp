package com.burujiyaseer.passwordmanager.domain.usecase.encrypt_decrypt_password

interface EncryptDecryptPassword {
    /**
     * encrypt [password] with a default fileName
     * @return [fileName]
     */
    suspend fun encryptPassword(
        password: String,
        fileName: String = System.currentTimeMillis().toString()
    ): String

    /**
     * decrypt encrypted password by providing [fileName]
     * @return the decrypted password or null in case of an error
     */
    suspend fun decryptPassword(
        fileName: String
    ): String?
}