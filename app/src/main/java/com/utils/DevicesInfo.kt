package com.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.InputStreamReader

object DevicesInfo {
//    fun getSerialNo(): String {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return getSerialNumber()
//        } else {
//            Build.SERIAL
//        }
//    }

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
    fun getMacAddress(): String? {
        try {
            val command = if (Build.VERSION.SDK_INT > 28) {
                "cat /sys/class/net/wlan0/address"
            } else {
                "su -c cat /sys/class/net/wlan0/address"
            }
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val macAddress = reader.readLine()
            process.waitFor()
            reader.close()
            return macAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getAppVersion(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}