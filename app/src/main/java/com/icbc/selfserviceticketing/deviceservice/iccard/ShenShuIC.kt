package com.icbc.selfserviceticketing.deviceservice.iccard

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.serialport.SerialPort
import android.serialport.SerialPortFinder
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.icbc.selfserviceticketing.deviceservice.IRFReader
import com.icbc.selfserviceticketing.deviceservice.NonContactDeviceListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author hao88
 * 商 N513 身份证 读卡模块（仅支持号码和头像)
 */
class ShenShuIC(private val port: String="/dev/ttyS2", private val cScope: CoroutineScope) :
    IRFReader.Stub() {
    private var listener: NonContactDeviceListener?=null
    private var isRun = AtomicBoolean(false)
    var device: UsbDevice? = null
    var serialJob: Job? = null
    private var mSerialPort: SerialPort? = null
    private var interval =500L
    companion object {
        private const val ACTION_USB_PERMISSION = "SS"
        private const val TAG = "IDN513v2"
        private const val DEV_BAUDRATE = 115200
    }

    private fun serialPortRead() {
        LogUtils.file("串口开启读卡 ${port}-${DEV_BAUDRATE}")
        //SerialPort.setSuPath("/system/xbin/su")
        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.allDevices
        val devicesPath = serialPortFinder.allDevicesPath
        Log.d(TAG, "serialPortRead: ${devices.contentDeepToString()}")
        Log.d(TAG, "serialPortRead: ${devicesPath.contentDeepToString()}")
        LogUtils.file("${devices.contentDeepToString()}")
        LogUtils.file("${devicesPath.contentDeepToString()}")
        Log.d(TAG, "serialPortRead: launch read isRun =${isRun.get()}")
        mSerialPort = SerialPort(File(port), DEV_BAUDRATE)
        mSerialPort?.let { sp ->
            serialJob = cScope.launch(Dispatchers.IO) {
                sp.inputStream.let { inputStream ->
                    LogUtils.file("open sp path=${sp.device.absolutePath} baudrate=${sp.baudrate} dataBits=${sp.dataBits} parity=${sp.parity}")
                    LogUtils.d("open sp path=${sp.device.absolutePath} baudrate=${sp.baudrate} dataBits=${sp.dataBits} parity=${sp.parity}")
                    isRun.set(true)
                    try {
                        while (isRun.get()) {
                            delay(500)
                            val size = inputStream.available()
                            if (size > 0) {
                                val buffer = ByteArray(32)
                                inputStream.read(buffer)
                                //convert to hex
                                val hexString = buffer.joinToString("") { "%02X".format(it) }
                                LogUtils.file("hex string = ",hexString)
                                val cardNumber = hexString.substring(14, 22)
                                LogUtils.file("card number =: $cardNumber")
                                val reverseCardNumber =reverseHexString(cardNumber)
                                val bundle =Bundle()
                                bundle.putString("ICCardType", "PSAM")
                                bundle.putString("ICCardContent", reverseCardNumber)
                                listener?.readCardCallBack("ICCard_Read_OnSuccess", bundle)
                                delay(interval)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LogUtils.file(e)
                    }
                }
            }
        }

    }
    fun extractCardNumber(buffer: ByteArray): String? {
        if (buffer.size < 8) {
            // 数据帧长度不足
            return null
        }

        // 校验帧头
        if (buffer[0] != 0xA8.toByte() || buffer[1] != 0x00.toByte() ||
            buffer[2] != 0x7B.toByte() || buffer[3] != 0x00.toByte()) {
            return null
        }

        // 校验帧尾
        if (buffer[buffer.lastIndex] != 0xA9.toByte()) {
            return null
        }

        // 解析数据长度
        val length = (buffer[4].toInt() shl 8) or (buffer[5].toInt() and 0xFF)
        if (buffer.size != length + 7) {
            // 数据长度不匹配
            return null
        }

        // 提取卡号
        val dataStartIndex = 6
        val dataEndIndex = buffer.lastIndex - 2
        val cardNumberBytes = buffer.sliceArray(dataStartIndex..dataEndIndex)
        return cardNumberBytes.joinToString(" ") { "%02X".format(it) }
    }

    override fun readICCardByNonContact(params: Bundle?, listener: NonContactDeviceListener?) {
        LogUtils.file("开始读卡")
        this.listener = listener
        serialPortRead()
    }
    fun reverseHexString(input: String): String {
        // 按每两位分组
        val bytes = input.chunked(2)
        // 反转分组后拼接
        return bytes.reversed().joinToString("")
    }
    override fun stopReadICCardByNonContact(params: Bundle?): Bundle? {
        LogUtils.file("停止读卡")
        listener = null
        isRun.set(false)
        serialJob?.cancel()
        mSerialPort?.let {
            LogUtils.file("tryClose 尝试关闭串口")
            it.tryClose()
        }
        return null
    }
}