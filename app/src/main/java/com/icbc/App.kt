package com.icbc

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import com.blankj.utilcode.util.TimeUtils
import io.sentry.android.core.SentryAndroid


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        SentryAndroid.init(context){ config ->
            config.dsn="https://35bad9859153b5a0f1c186d6a9e7b9ef@o4508631282155520.ingest.us.sentry.io/4508922872332288"
            config.isDebug=false
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

    }

}
fun getAndroidID(): String {
    val androidID = Settings.Secure.getString(App.context.contentResolver, Settings.Secure.ANDROID_ID)
    return androidID
}

fun today(): String {
    return TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyyMMdd"))
}