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
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINE
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINEDETECT
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP
import java.nio.charset.StandardCharsets
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 励能T321/T331
 */
class EPSONUSBPrinter(private val context: Context, private val config: Config) : IProxyPrinter {
    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
        private const val MAX_USBFS_BUFFER_SIZE = 10240
        private const val DENSITY: Int = 203 // 打印机分辨率
        private const val DPI = 8 // (203 / 25.4f)
        private const val TIMEOUT = 5000
        private const val TAG = "EPSONUSBPrinter"
    }

    private var usbManager: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var usbConnection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var usbEndpoint: UsbEndpoint? = null
    private var bitmapPrinter: BitmapPrinterV4? = null
    private var hasPermission = false
    private var pageWidth = 0
    private var pageHeight = 0

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    hasPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            && device != null
                    usbDevice = device
                }
            }
        }
    }

    init {
        initialize()
        connectUsbPrinter()
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
        usbEndpoint = usbInterface?.getEndpoint(0)
        usbConnection = manager.openDevice(device).apply {
            claimInterface(usbInterface, true)
        }
        true
    }.getOrElse {
        LogUtils.e(TAG, "Failed to open port", it)
        false
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
        usbEndpoint = null
        usbManager = null
        usbDevice = null
        usbInterface = null
    }

    private fun sendCommand(command: String): Boolean = usbConnection?.let { connection ->
        runCatching {
            val data = command.toByteArray(StandardCharsets.UTF_8)
            connection.bulkTransfer(usbEndpoint, data, data.size, TIMEOUT) >= 0
        }.getOrElse {
            LogUtils.e(TAG, "Failed to send command: $command", it)
            false
        }
    } ?: false

    private fun printerBitmap(bitmap: Bitmap): Int = runCatching {
        Log.d(TAG, "Printing bitmap, size=${bitmap.byteCount / 1024.0f}KB")
        sendCommand("SIZE ${config.width} mm,${config.height} mm\r\n")
        sendPaperTypeCommand()
        sendCommand("CLS\r\n")
        sendBitmap(0, 0, bitmap)
        sendCommand("PRINT 1\r\n")
        Log.d(TAG, "Print completed")
        0
    }.getOrElse {
        Log.e(TAG, "Print failed", it)
        -1
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
                val gray = Color.red(bitmap.getPixel(x, y)) // Assuming monochrome
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
            usbConnection?.bulkTransfer(usbEndpoint, chunk, chunk.size, TIMEOUT)
            offset += chunkSize
            Log.d(TAG, "sendLargeData:  offset=${offset}")
        }
        Log.d(TAG, "sendLargeData: END")
    }

    private fun Int.toPix(): Int = this * DPI

    override fun OpenDevice(deviceId: Int, deviceFile: String?, port: String?, param: String?): Int = 0

    override fun CloseDevice(deviceId: Int): Int {
        bitmapPrinter?.recycle()
        closePort()
        return 0
    }

    override fun getStatus(): Int = 0

    override fun setPageSize(format: Bundle?): Int {
        format ?: return -1
        with(format) {
            pageWidth = getInt("pageW")
            pageHeight = getInt("pageH")
            val direction = getInt("direction")
            val offsetX = getInt("OffsetX")
            val offsetY = getInt("OffsetY")
            Log.d(TAG, "setPageSize: pageW=${pageWidth},pageH=${pageHeight},direction=${direction},offsetX=${offsetX},offsetY=${offsetY}")
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

    override fun startPrintDoc(): Int = 0

    override fun addText(format: Bundle, text: String): Int {
        with(format) {
            val fontSize = getInt("fontSize")
            val rotation = getInt("rotation")
            val iLeft = getInt("iLeft")
            val iTop = getInt("iTop")
            val align = getInt("align")
            val pageWidth = getInt("pageWidth")
            Log.d(TAG, "addText: fontSis=${fontSize},rotation=${rotation},iLeft=${iLeft},iTop=${iTop},align=${align},pageWidth=${pageWidth}")
            bitmapPrinter?.addText(
                text,
                fontSize * PRINT_FONT_SCALE ,
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
}