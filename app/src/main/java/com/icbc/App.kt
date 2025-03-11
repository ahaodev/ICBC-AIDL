package com.icbc

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import com.blankj.utilcode.util.TimeUtils
import io.sentry.android.core.BuildConfig
import io.sentry.android.core.SentryAndroid


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        SentryAndroid.init(context){ config ->
            config.dsn="https://35bad9859153b5a0f1c186d6a9e7b9ef@o4508631282155520.ingest.us.sentry.io/4508922872332288"
            config.isDebug=BuildConfig.DEBUG
            config.setTag("SN", getSerialNo())
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

    }
    fun getSerialNo(): String {
        return if (Build.VERSION.SDK_INT >= 30) {
            return getSerialNumber()
        } else {
            Build.SERIAL
        }
    }

    fun getSerialNumber(): String {
        return try {
            val process = Runtime.getRuntime().exec("getprop ro.serialno")
            val reader = process.inputStream.bufferedReader()
            val serialNumber = reader.readLine()
            reader.close()
            serialNumber
        } catch (e: Exception) {
            e.printStackTrace()
            "null"
        }
    }

}

fun today(): String {
    return TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyyMMdd"))
}