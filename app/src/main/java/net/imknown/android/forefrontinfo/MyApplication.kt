package net.imknown.android.forefrontinfo

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.*
import java.io.File

@SuppressLint("Registered")
open class MyApplication : Application(), CoroutineScope by MainScope() {

    companion object {
        lateinit var instance: MyApplication

        fun getDownloadDir() = getFileDir(Environment.DIRECTORY_DOWNLOADS)

        private fun getFileDir(type: String): File {
            val externalFilesDir = instance.getExternalFilesDir(type)
            return if (externalFilesDir != null && externalFilesDir.exists()) {
                externalFilesDir
            } else {
                instance.filesDir.resolve(type)
            }
        }

        fun getMyString(@StringRes resId: Int) =
            instance.getString(resId)

        fun getMyString(@StringRes resId: Int, vararg formatArgs: Any?) =
            instance.getString(resId, *formatArgs)
    }

    override fun onCreate() {
        super.onCreate()

        launch(Dispatchers.IO) {
            instance = this@MyApplication
        }
    }

    fun setMyTheme(themesValue: String) = launch(Dispatchers.Default) {
        @AppCompatDelegate.NightMode val mode = when (themesValue) {
            getMyString(R.string.interface_themes_follow_system_value) -> {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            getMyString(R.string.interface_themes_power_saver_value) -> {
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
            getMyString(R.string.interface_themes_always_light_value) -> {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            getMyString(R.string.interface_themes_always_dark_value) -> {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else -> {
                AppCompatDelegate.MODE_NIGHT_YES
            }
        }

        withContext(Dispatchers.Main) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}