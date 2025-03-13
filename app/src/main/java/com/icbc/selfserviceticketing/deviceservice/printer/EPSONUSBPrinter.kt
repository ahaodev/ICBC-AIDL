package com.icbc.selfserviceticketing.deviceservice.printer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINE
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINEDETECT
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_HOT
import com.icbc.selfserviceticketing.deviceservice.printer.Constants.ERROR
import com.icbc.selfserviceticketing.deviceservice.printer.Constants.MEIHEIBIAO
import com.icbc.selfserviceticketing.deviceservice.printer.Constants.MEIZHILE
import com.icbc.selfserviceticketing.deviceservice.printer.Constants.OK
import com.icbc.selfserviceticketing.deviceservice.printer.Constants.QUEZHI
import java.nio.charset.StandardCharsets
import kotlin.math.min
import androidx.core.graphics.get


/**
 * 励能T321/T331
 */
class EPSONUSBPrinter(private val context: Context, private val config: Config) : IProxyPrinter {
    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
        private var MAX_USBFS_BUFFER_SIZE = 10240
        private var maxReadBuffer = 10240
        private const val DENSITY: Int = 203 // 打印机分辨率
        private const val DPI = 8 // (203 / 25.4f)
        private const val TIMEOUT = 5000
        private const val TAG = "EPSONUSBPrinter"

        // Status Polling Command Hex: 1B 21 3F 0A
        private val READ_STATUS: ByteArray = byteArrayOf(0x1B, 0x21, 0x3F, 0x0A) // <ESC>!?
    }

    private var usbManager: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var usbConnection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var usbEndpointOut: UsbEndpoint? = null
    private var usbEndpointIn: UsbEndpoint? = null
    private var bitmapPrinter: BitmapPrinterV4? = null
    private var hasPermission = false
    private var pageWidth = 0
    private var pageHeight = 0
    private var usbStatus = 0 // 0:正常 1:错误
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    hasPermission =
                        intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                                && device != null
                    usbDevice = device
                }
            }
        }
    }

    init {
        initialize()
        usbStatus = connectUsbPrinter()
        if (usbStatus != 0) {
            LogUtils.e(TAG, "Failed to open port")
            ToastUtils.showLong("Failed to open port")
        }
    }

    // read usb printer status ,send READ_STATUS command
    fun checkPrinterStatus(): List<String> {
        val statusCommand = READ_STATUS
        val sendStatus =
            usbConnection?.bulkTransfer(usbEndpointOut, statusCommand, statusCommand.size, TIMEOUT)
        val statusBuffer = ByteArray(1)
        val readStatus =
            usbConnection?.bulkTransfer(usbEndpointIn, statusBuffer, statusBuffer.size, TIMEOUT)
        val statusBytes = readStatus(statusBuffer)
        return listOf(
            if (statusBytes[0] == 1.toByte()) "缺纸" else "",
            if (statusBytes[1] == 1.toByte()) "面盖打开错误" else "",
            if (statusBytes[2] == 1.toByte()) "切刀错误" else "",
            if (statusBytes[3] == 1.toByte()) "黑标或孔洞定位错误" else "",
            "", // byteArray[4] is undefined
            if (statusBytes[5] == 1.toByte()) "纸将尽" else "",
            if (statusBytes[6] == 1.toByte()) "错误(出纸嘴有纸)" else "",
            if (statusBytes[7] == 1.toByte()) "打印中" else ""
        )
    }

    // read status byte array
    private fun readStatus(read: ByteArray): ByteArray {
        val byteArr = ByteArray(8) // 一个字节八位
        var value = read[0].toInt()
        for (i in 0..7) {
            byteArr[i] = (value and 1).toByte() // 获取最低位
            value = value shr 1 // 每次右移一位
        }
        return byteArr
    }

    private fun initialize(): Result<Unit> = runCatching {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        registerUsbReceiver()
        findAndRequestUsbDevice()
    }.onFailure { LogUtils.e(TAG, "Initialization failed", it) }

    private fun registerUsbReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        ContextCompat.registerReceiver(
            context, usbReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun findAndRequestUsbDevice() {
        usbManager?.deviceList?.values?.find { it.vendorId == 5251 }?.let { device ->
            usbDevice = device
            val permissionIntent = PendingIntent.getBroadcast(
                context, 0, Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            usbManager?.requestPermission(device, permissionIntent)
        } ?: LogUtils.w(TAG, "No USB printer found with vendorId 5251")
    }

    private fun connectUsbPrinter(): Int {
        val manager = usbManager ?: return 1
        val device = usbDevice ?: return 1

        if (!manager.hasPermission(device)) {
            LogUtils.e(TAG, "No USB permission")
            return 1
        }

        return if (openPort(manager, device)) 0 else 1
    }

    private fun openPort(manager: UsbManager, device: UsbDevice): Boolean = runCatching {
        usbInterface = device.getInterface(0)
        usbInterface?.let {
            assignEndpoint(it)
        }

//        usbEndpointOut = usbInterface?.getEndpoint(0)
//        usbEndpointIn = usbInterface?.getEndpoint(128)
        usbConnection = manager.openDevice(device).apply {
            claimInterface(usbInterface, true)
        }
        true
    }.getOrElse {
        LogUtils.e(TAG, "Failed to open port", it)
        false
    }

    private fun assignEndpoint(usbInterface: UsbInterface) {
        for (i in 0 until usbInterface.endpointCount) {
            val ep = usbInterface.getEndpoint(i)
            LogUtils.i("UsbEndpoint类型：${ep.type}")
            when (ep.type) {
                2 -> {
                    if (ep.direction == 0) {
                        usbEndpointOut = ep
                        MAX_USBFS_BUFFER_SIZE = usbEndpointOut?.maxPacketSize ?: 0
                    }
                    if (ep.direction == 128) {
                        usbEndpointIn = ep
                        maxReadBuffer = usbEndpointIn?.maxPacketSize ?: 0
                    }
                }

                3 -> {
                    if (ep.direction == 0) {
                        usbEndpointOut = ep
                        MAX_USBFS_BUFFER_SIZE = usbEndpointOut?.maxPacketSize ?: 0
                    }
                    if (ep.direction == 128) {
                        usbEndpointIn = ep
                        maxReadBuffer = usbEndpointIn?.maxPacketSize ?: 0
                    }
                }
            }
        }
    }

    private fun closePort(): Boolean = usbConnection?.let { connection ->
        runCatching {
            Thread.sleep(1300)
            connection.releaseInterface(usbInterface)
            connection.close()
            resetConnection()
            true
        }.getOrElse {
            LogUtils.e(TAG, "Failed to close port", it)
            false
        }
    } ?: false

    private fun resetConnection() {
        usbConnection = null
        usbEndpointOut = null
        usbManager = null
        usbDevice = null
        usbInterface = null
    }

    private fun sendCommand(command: String): Boolean = usbConnection?.let { connection ->
        Log.d(TAG, "> $command")
        runCatching {
            val data = command.toByteArray(StandardCharsets.UTF_8)
            connection.bulkTransfer(usbEndpointOut, data, data.size, TIMEOUT) >= 0
        }.getOrElse {
            LogUtils.e(TAG, "Failed to send command: $command", it)
            false
        }
    } ?: false

    private fun printerBitmap(bitmap: Bitmap): Int = runCatching {
        val status = checkPrinterStatus()
        //            if (statusBytes[0] == 1.toByte()) "缺纸" else "",
        //            if (statusBytes[1] == 1.toByte()) "面盖打开错误" else "",
        //            if (statusBytes[2] == 1.toByte()) "切刀错误" else "",
        //            if (statusBytes[3] == 1.toByte()) "黑标或孔洞定位错误" else "",
        //            "", // byteArray[4] is undefined
        //            if (statusBytes[5] == 1.toByte()) "纸将尽" else "",
        //            if (statusBytes[6] == 1.toByte()) "错误(出纸嘴有纸)" else "",
        //            if (statusBytes[7] == 1.toByte()) "打印中" else ""
        when {
            status[0] == "缺纸" -> {
                return@runCatching QUEZHI
            }

            status[3] == "黑标或孔洞定位错误" -> {
                return@runCatching MEIHEIBIAO
            }

            status[5] == "纸将尽" -> {
                return@runCatching MEIZHILE
            }
        }

        Log.d(TAG, "Printing bitmap, size=${bitmap.byteCount / 1024.0f}KB")
        sendCommand("SIZE ${config.width} mm,${config.height} mm\r\n")
        sendPaperTypeCommand()
        sendCommand("CLS\r\n")
        if (config.printerType == PAPER_TYPE_HOT) {
            sendCommand("DIRECTION 0,0\r\n")
            sendCommand("REFERENCE 0,0\r\n")
        }
        sendBitmap(0, 0, bitmap)
        sendCommand("PRINT 1\r\n")
        if (config.enableCutter){
            sendCommand("FEED 20\r\n")
            sendCommand("CUT\r\n")
        }
        Log.d(TAG, "Print completed")
        OK
    }.getOrElse {
        Log.e(TAG, "Print failed", it)
        ERROR
    }

    private fun sendPaperTypeCommand() {
        when (config.paperType) {
            PAPER_TYPE_CAP -> sendCommand("GAP ${config.margin} mm,0\r\n")
            PAPER_TYPE_BLINE -> sendCommand("BLINE ${config.margin} mm,0\r\n")
            PAPER_TYPE_BLINEDETECT -> sendCommand("BLINEDETECT [${config.height * 12},${config.margin * 12}]")
        }
    }

    private fun sendBitmap(x: Int, y: Int, bitmap: Bitmap) {
        val widthBytes = (bitmap.width + 7) / 8
        val command = "BITMAP $x,$y,$widthBytes,${bitmap.height},0,"
        val data = convertBitmapToByteArray(bitmap, widthBytes)

        sendCommand(command)
        sendLargeData(data)
        sendCommand("\r\n")
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap, widthBytes: Int): ByteArray {
        Log.d(TAG, "convertBitmapToByteArray START")
        val stream = ByteArray(widthBytes * bitmap.height) { -1 }

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val gray = Color.red(bitmap[x, y]) // Assuming monochrome
                if (gray == 0) {
                    val index = y * widthBytes + x / 8
                    val bitPosition = 128 shr (x % 8)
                    stream[index] = (stream[index].toInt() xor bitPosition).toByte()
                }
            }
        }
        Log.d(TAG, "convertBitmapToByteArray END")
        return stream
    }

    private fun sendLargeData(data: ByteArray) {
        Log.d(TAG, "sendLargeData: START")
        var offset = 0
        Log.d(TAG, "sendLargeData: bufferSize =${data.size}")
        while (offset < data.size) {
            val chunkSize = min(MAX_USBFS_BUFFER_SIZE, data.size - offset)
            val chunk = data.copyOfRange(offset, offset + chunkSize)
            usbConnection?.bulkTransfer(usbEndpointOut, chunk, chunk.size, TIMEOUT)
            offset += chunkSize
        }
        Log.d(TAG, "sendLargeData: END")
    }

    private fun Int.toPix(): Int = this * DPI

    override fun OpenDevice(
        deviceId: Int,
        deviceFile: String?,
        port: String?,
        param: String?
    ): Int = 0

    override fun CloseDevice(deviceId: Int): Int {
        bitmapPrinter?.recycle()
        closePort()
        return 0
    }

    override fun getStatus(): Int {
        return usbStatus
    }

    override fun setPageSize(format: Bundle?): Int {
        format ?: return -1
        with(format) {
            pageWidth = getInt("pageW")
            pageHeight = getInt("pageH")
            val direction = getInt("direction")
            val offsetX = getInt("OffsetX")
            val offsetY = getInt("OffsetY")
            Log.d(
                TAG,
                "setPageSize: pageW=${pageWidth},pageH=${pageHeight},direction=${direction},offsetX=${offsetX},offsetY=${offsetY}"
            )
            bitmapPrinter = BitmapPrinterV4(config).apply {
                setPageSize(
                    pageWidth.toPix(),
                    pageHeight.toPix(),
                    direction,
                    offsetX.toPix(),
                    offsetY.toPix()
                )
            }
            Log.d(TAG, "Page size set: $pageWidth x $pageHeight")
            return 0
        }
    }

    override fun startPrintDoc(): Int {
        return 0
    }

    override fun addText(format: Bundle, text: String): Int {
        with(format) {
            val fontSize = getInt("fontSize")
            val rotation = getInt("rotation")
            val iLeft = getInt("iLeft")
            val iTop = getInt("iTop")
            val align = getInt("align")
            val pageWidth = getInt("pageWidth")
            Log.d(
                TAG,
                "addText: fontSis=${fontSize},rotation=${rotation},iLeft=${iLeft},iTop=${iTop},align=${align},pageWidth=${pageWidth}"
            )
            bitmapPrinter?.addText(
                text,
                fontSize * PRINT_FONT_SCALE,
                rotation,
                iLeft.toPix(),
                iTop.toPix(),
                align,
                pageWidth.toPix()
            )
            return 0
        }
    }

    override fun addQrCode(format: Bundle?, qrCode: String?): Int {
        format ?: return -1
        qrCode ?: return -1
        with(format) {
            val iLeft = getInt("iLeft")
            val iTop = getInt("iTop")
            val expectedHeight = getInt("expectedHeight")
            Log.d(TAG, "addQrCode: iLeft=${iLeft},iTop=${iTop},expectedHeight=${expectedHeight}")
            bitmapPrinter?.addQrCode(
                iLeft.toPix(),
                iTop.toPix(),
                expectedHeight.toPix(),
                qrCode
            )
            return 0
        }
    }

    override fun addImage(format: Bundle?, imageData: String?): Int = 0

    override fun endPrintDoc(): Int {
        bitmapPrinter?.drawEnd()?.let { bitmap ->
            val rotated = bitmapPrinter?.rotateBitmap(bitmap, config.rotation.toFloat())
            return printerBitmap(rotated ?: bitmap).also {
                bitmap.recycle()
                bitmapPrinter?.recycle()
                bitmapPrinter = null
            }
        } ?: return -1
    }

    override fun selfTest(): Int {
        sendCommand("SELFTEST\r\n")
        return 0
    }
}