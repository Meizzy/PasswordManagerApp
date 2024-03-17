package com.burujiyaseer.passwordmanager.domain.usecase.get_website_fav_icon_url

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

private const val GOOGLE_FAV_ICON_BASE_URL = "https://www.google.com/s2/favicons?domain="
private const val FAV_ICON_DIR_NAME = "Favicons"
private const val PNG_EXTENSION_NAME = ".png"
private const val COMPRESSION_QUALITY = 0

class DefaultSaveFavIconFromWebsiteUrl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : SaveFavIconFromWebsiteUrl {

    override suspend fun invoke(websiteUrl: String): String? = kotlin.runCatching {
        withContext(Dispatchers.IO) {
            // get website fav icon bitmap from google services
            val faviconUrl = "$GOOGLE_FAV_ICON_BASE_URL$websiteUrl"
            val faviconBitmap = BitmapFactory.decodeStream(
                URL(faviconUrl).openConnection().getInputStream()
            )

            val isExternalStorageAvailable =
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            val parentFile = File(
                if (isExternalStorageAvailable) appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) else appContext.externalCacheDir,
                FAV_ICON_DIR_NAME
            )
            if (!parentFile.exists()) parentFile.mkdir()
            val destinationFile = File(
                parentFile,
                "${System.currentTimeMillis()}$PNG_EXTENSION_NAME"
            )
            destinationFile.createNewFile()
            val fileOutputStream = FileOutputStream(destinationFile)
            // compress the image
            faviconBitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, fileOutputStream)
            fileOutputStream.close()

            //return absolute path of saved file
            destinationFile.absolutePath
        }
    }.onFailure { utilLog("error: $it") }.getOrNull()

}