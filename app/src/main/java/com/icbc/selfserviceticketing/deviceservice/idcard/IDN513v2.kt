package com.icbc.selfserviceticketing.deviceservice.idcard

//import com.sensor.idcard.IDCardDevice
//import com.sensor.idcard.IDCardInfo
import android.content.Context
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.serialport.SerialPort
import android.util.Log
import com.icbc.selfserviceticketing.deviceservice.DeviceListener
import com.zkteco.android.IDReader.IDPhotoHelper
import com.zkteco.android.IDReader.WLTService
import kotlinx.coroutines.*
import java.io.File

/**
 * @author hao88
 * 商 N513 身份证 读卡模块（仅支持号码和头像)
 */
class IDN513v2(private val context: Context, private val cScope: CoroutineScope) : IProxyIDCard {
    private var idCall: ((Int, String, Bundle) -> Unit)? = null
    var isOpen = true
    var device: UsbDevice? = null
    var serialJob: Job? = null
    private var mSerialPort: SerialPort? = null

    companion object {
        private const val ACTION_USB_PERMISSION = "N513"
        private const val TAG = "IDN513v2"
        private const val DEV_PORT = "/dev/ttyUSB0"
        private const val DEV_BAUDRATE = 9600
        private val IDCard_Read_OnProgress = "IDCard_Read_OnProgress"
        private val IDCard_Read_OnError = "IDCard_Read_OnError"
        private val IDCard_Read_OnSuccess = "IDCard_Read_OnSuccess"
    }

    init {
        Log.d(TAG, "init IDN513v2 ")
        idCall?.let {
            Log.d(TAG, "init : idCall !=null ")
            start(it)
        }
    }

    private fun start(idCall: (Int, String, Bundle) -> Unit) {
        Log.d(TAG, "start: 开始读卡$idCall")
        this.idCall = idCall
        if (isOpen) return
        isOpen = true
        serialPortRead()
    }

    private fun stop() {
        isOpen = false
        serialJob?.cancel()
        mSerialPort?.let {
            it.close()
            it.inputStream.close()
            it.outputStream.close()
        }
    }

    private fun serialPortRead() {
        SerialPort.setSuPath("/system/xbin/su")
        mSerialPort = SerialPort(File(DEV_PORT), DEV_BAUDRATE)
        val inputStream = mSerialPort!!.inputStream
        serialJob = cScope.launch(Dispatchers.IO) {
            while (isOpen) {
                delay(600)
                try {
                    val size = inputStream.available()
                    if (size <= 0) continue
                    val buffer = ByteArray(1042)
                    inputStream.read(buffer)
                    val id = String(buffer.copyOfRange(0, 18))
                    val photo = buffer.copyOfRange(18, buffer.size)
                    var bundle = Bundle()
                    bundle.putString("IDName", id)
                    val imgBuffer = ByteArray(WLTService.imgLength)
                    if (photo.isNotEmpty()) {
                        if (1 == WLTService.wlt2Bmp(photo, imgBuffer)) {
                            val bitmap = IDPhotoHelper.Bgr2Bitmap(imgBuffer)
                            bundle.putParcelable("IDImage", bitmap)
                        }
                    }
                    idCall?.let {
                        it(0, "success", bundle)
                    }
                    delay(3000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun OpenDevice(
        DeviceID: Int,
        deviceFile: String?,
        szPort: String?,
        szParam: String?
    ): Int {
        Log.d(TAG, "OpenDevice: 开始读卡")
        return 0
    }

    override fun CloseDevice(DeviceID: Int): Int {
        Log.d(TAG, "CloseDevice: 关闭读卡")
        stop()
        return 0
    }

    override fun GetDeviceStatus(DeviceID: Int): Int {
        return 0
    }

    override fun commandNonBlock(cmd: String?, params: Bundle?, listener: DeviceListener) {
        Log.d(TAG, "commandNonBlock: 注册监听")
        start { status: Int, type: String, build: Bundle ->
            Log.d(TAG, "commandNonBlock: 读到卡 status=$status type=$type")
            listener.callBack(IDCard_Read_OnSuccess, build)
        }
    }

    override fun commandBlock(cmd: String?, params: Bundle?): Bundle? {
        return null
    }


}