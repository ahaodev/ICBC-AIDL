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
import com.icbc.selfserviceticketing.deviceservice.idcard.ID180v2
import com.icbc.selfserviceticketing.deviceservice.idcard.IDM40
import com.icbc.selfserviceticketing.deviceservice.scanner.ScannerSuperLead

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
                LogUtils.file("插入未知的USB设备")
                LogUtils.file(device.toString())
                if (isSelfDevice(device)){
                    restart(context)
                }
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                LogUtils.d("拔出未知的USB设备", device.toString())
                LogUtils.file("拔出未知的USB设备")
                LogUtils.file(device.toString())
                if (isSelfDevice(device)){
                    restart(context)
                }
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

    private fun isSelfDevice(device: UsbDevice): Boolean {
        if (device.vendorId == ID180v2.VID && device.productId == ID180v2.PID) {
            LogUtils.file("ZKT USB设备", device.toString())
            return true
        }
        if (device.vendorId == IDM40.mVendorId && device.productId == IDM40.mProductId) {
            LogUtils.file("IDM40 USB设备", device.toString())
            return true
        }
        if (device.vendorId == ScannerSuperLead.DEFAULT_VENDOR_ID) {
            LogUtils.file("ScannerSuperLead USB设备", device.toString())
            return true
        }
        //mVendorId=4611,mProductId=311 TSC310E打印机
        if (device.vendorId==4611&&device.productId==311){
            return true
        }
        if (device.vendorId==5251&&device.productId==21571&&device.productName=="LENOO-T321BS"){
            return true
        }
        return false
    }
}
