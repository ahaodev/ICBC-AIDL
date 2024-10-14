package com.utils

import android.os.Build
import java.io.BufferedReader
import java.io.InputStreamReader

object SystemUtil {
    fun getSN(): String? {
        if (Build.VERSION.SDK_INT < 28) {
            return Build.SERIAL
        }
        val cmd = "getprop ro.serialno"
        try {
            val process = Runtime.getRuntime().exec(cmd)
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

}