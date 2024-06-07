package com.icbc.selfserviceticketing.deviceservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * USB 插拔监听
 * @date 2022/10/9
 * @author 锅得铁
 * @since v1.0
 */
class UsbBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val device =
            intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice? ?: return
        LogUtils.d(action, device.toString())
        when (action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                LogUtils.d("插入未知的USB设备", device.toString())
                LogUtils.file("插入未知的USB设备", device.toString())
                restart(context)
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                LogUtils.d("拔出未知的USB设备", device.toString())
                LogUtils.file("拔出未知的USB设备", device.toString())
                restart(context)
            }
        }
    }

    private fun restart(context: Context) {
        LogUtils.file("App restart")
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startService(intent)
        Intent(context, DeviceService::class.java).also {
            context.stopService(it)
        }
        //杀掉以前进程
        android.os.Process.killProcess(android.os.Process.myPid())
    }

}
