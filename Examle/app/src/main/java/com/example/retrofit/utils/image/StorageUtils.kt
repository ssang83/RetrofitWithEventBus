package com.example.retrofit.utils.image

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.util.Log
import java.io.File
import java.io.IOException

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
object StorageUtils {

    private val LOGTAG = "StorageUtils"


    private val EXTERNAL_STORAGE_PERMISSION = "android.permission" + ".WRITE_EXTERNAL_STORAGE"
    private val INDIVIDUAL_DIR_NAME = "uil-images"

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * *("/Android/data/[app_package_name]/cache")* if card is mounted and app has appropriate
     * permission. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache [directory][File].<br></br> **NOTE:** Can be null in some unpredictable
     * cases (if SD card is unmounted and [Context.getCacheDir()][Context.getCacheDir]
     * returns null).
     */
    fun getCacheDirectory(context: Context): File {
        return getCacheDirectory(context, true)
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * *("/Android/data/[app_package_name]/cache")* (if card is mounted and app has appropriate
     * permission) or on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache [directory][File].<br></br> **NOTE:** Can be null in some unpredictable
     * cases (if SD card is unmounted and [Context.getCacheDir()][Context.getCacheDir]
     * returns null).
     */
    fun getCacheDirectory(context: Context, preferExternal: Boolean): File {
        var appCacheDir: File? = null
        var externalStorageState: String
        try {
            externalStorageState = Environment.getExternalStorageState()
        } catch (e: NullPointerException) { // (sh)it happens (Issue #660)
            externalStorageState = ""
        } catch (e: IncompatibleClassChangeError) {
            externalStorageState = ""
        }

        if (preferExternal && MEDIA_MOUNTED == externalStorageState && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context)
        }
        if (appCacheDir == null) {
            appCacheDir = context.cacheDir
        }
        if (appCacheDir == null) {
            val cacheDirPath = "/data/data/" + context.packageName + "/cache/"
            Log.w(
                LOGTAG, "Can't define system cache directory! '%s' will be used.",
                Throwable(cacheDirPath)
            )
            appCacheDir = File(cacheDirPath)
        }
        return appCacheDir
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader).
     * Cache directory will be created on SD card *
     * ("/Android/data/[app_package_name]/cache/uil-images")
     * *  if card is mounted and app has appropriate permission. Else - Android defines cache
     * directory on device's file system.
     *
     * @param context Application context
     * @return Cache [directory][File]
     */
    fun getIndividualCacheDirectory(context: Context): File {
        return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME)
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader).
     * Cache directory will be created on SD card *
     * ("/Android/data/[app_package_name]/cache/uil-images")
     * *  if card is mounted and app has appropriate permission. Else - Android defines cache
     * directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache [directory][File]
     */
    fun getIndividualCacheDirectory(context: Context, cacheDir: String): File {
        val appCacheDir = getCacheDirectory(context)
        var individualCacheDir = File(appCacheDir, cacheDir)
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir
            }
        }
        return individualCacheDir
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by
     * defined path if card is mounted and app has appropriate permission. Else - Android defines
     * cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache [directory][File]
     */
    fun getOwnCacheDirectory(context: Context, cacheDir: String): File? {
        var appCacheDir: File? = null
        if (MEDIA_MOUNTED == Environment.getExternalStorageState() && hasExternalStoragePermission(context)) {
            appCacheDir = File(Environment.getExternalStorageDirectory(), cacheDir)
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by
     * defined path if card is mounted and app has appropriate permission. Else - Android defines
     * cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache [directory][File]
     */
    fun getOwnCacheDirectory(
        context: Context, cacheDir: String,
        preferExternal: Boolean
    ): File? {
        var appCacheDir: File? = null
        if (preferExternal && MEDIA_MOUNTED == Environment.getExternalStorageState() && hasExternalStoragePermission(
                context
            )
        ) {
            appCacheDir = File(Environment.getExternalStorageDirectory(), cacheDir)
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir
    }

    private fun getExternalCacheDir(context: Context): File? {
        val dataDir = File(
            File(Environment.getExternalStorageDirectory(), "Android"),
            "data"
        )
        val appCacheDir = File(File(dataDir, context.packageName), "cache")
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.w(LOGTAG, "Unable to create external cache directory")
                return null
            }
            try {
                File(appCacheDir, ".nomedia").createNewFile()
            } catch (e: IOException) {
                Log.i(
                    LOGTAG,
                    "Can't create \".nomedia\" file in application external cache directory"
                )
            }

        }
        return appCacheDir
    }

    private fun hasExternalStoragePermission(context: Context): Boolean {
        val perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION)
        return perm == PackageManager.PERMISSION_GRANTED
    }
}