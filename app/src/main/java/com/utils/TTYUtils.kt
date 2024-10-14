package com.utils

import android.serialport.SerialPort
import android.serialport.SerialPortFinder
import androidx.core.util.Pair

object TTYUtils {

    fun getSerialPort(): Pair<Array<String>, Array<String>> {
        SerialPort.setSuPath("/system/xbin/su")
        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.allDevices
        val devicesPath = serialPortFinder.allDevicesPath
        return Pair(devices, devicesPath)
    }

}