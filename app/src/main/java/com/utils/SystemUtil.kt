package com.utils

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
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
    fun getIMEI(): String? {
        return "1122000998987765"
    }

    fun getIMSI(): String? {
        return "310150123456789"
    }

    fun getICCID(): String? {
        return "XXXXXX0MFSSYYGXXXXXX"
    }

}