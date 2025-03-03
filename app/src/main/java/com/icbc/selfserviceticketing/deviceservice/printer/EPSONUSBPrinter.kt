package com.icbc.selfserviceticketing.deviceservice.printer

import android.app.Activity
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
import android.os.Parcelable
import android.util.Log
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINE
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINEDETECT
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP

/**
 *
 * 溪水公园 h 203  w 78  dot 300
 * 溪水公园 h 203  w 78  dot 300
 *
 */
class EPSONUSBPrinter(private val context: Context, val config: Config) : IProxyPrinter {
    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
        private var mUsbManager: UsbManager? = null
        private var mPermissionIntent: PendingIntent? = null
        private var hasPermissionToCommunicate = false
        private var device: UsbDevice? = null
        private val MAX_USBFS_BUFFER_SIZE = 1024*10
        private var DPI = 12
        private var TIMEOUT = 5000
        val TAG = "TSCUsbPrinter"
    }

    private var pageW = 0
    private var pageH = 0
    private var usbEndpointIn: UsbEndpoint? = null
    private var usbEndpointOut: UsbEndpoint? = null
    private var mUsbConnection: UsbDeviceConnection? = null
    private var mUsbendpoint: UsbEndpoint? = null
    private var mUsbIntf: UsbInterface? = null
    private var bitmapPrinter: BitmapPrinterV3? = null
    private var mUsbDevice: UsbDevice? = null

    // Catches intent indicating if the user grants permission to use the USB device
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    mUsbDevice =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true
                        }
                    }
                }
            }
        }
    }

    init {
        onInit()
        connectUsbPrinter()
    }

    private fun onInit(): Int {
        LogUtils.file("Init")
        mUsbManager = context.getSystemService(Activity.USB_SERVICE) as UsbManager
        mPermissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        ContextCompat.registerReceiver(
            context,
            mUsbReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        val accessoryList = mUsbManager!!.accessoryList
        val deviceList = mUsbManager!!.deviceList
        LogUtils.d("Detect ", deviceList.size.toString() + " USB device(s) found")
        val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next()
            LogUtils.d(device.toString())
            if (device!!.vendorId == 5251) {
                //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                LogUtils.d(device.toString())
                break
            }
        }
        //-----------start-----------
        val mPermissionIntent: PendingIntent
        mPermissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        mUsbManager!!.requestPermission(device, mPermissionIntent)
        return 0
    }

    fun connectUsbPrinter(): Int {
        LogUtils.file("connectUsbPrinter")
        if (!mUsbManager!!.hasPermission(device)) {
            Log.e(TAG, "connectUsbPrinter: USB无访问权限")
            LogUtils.file(TAG, "connectUsbPrinter: USB无访问权限")
            return 1
        }

        openPort(
            mUsbManager!!,
            device!!
        )
        return 0
    }
    private fun  toDot(){

    }
    private fun printerBitmap(bitmap: Bitmap): Int {
        LogUtils.d("Start print ",config.toString())
        LogUtils.file("开始打印,bitmap size=${bitmap.byteCount / 1024.0f}KB")
        //sendCommand("SIZE 70 mm,172.46 mm\r\n")//天眼的实际设定
        sendCommand("SIZE ${config.width} mm,${config.height} mm\r\n")
        //sendCommand("GAP 4.66 mm,0\r\n")//天眼的实际设定
        when(config.paperType){
            PAPER_TYPE_CAP->{
                sendCommand("GAP ${config.margin} mm,0\r\n")
            }
            PAPER_TYPE_BLINE->{
                sendCommand("BLINE ${config.margin} mm,0\r\n")
            }
            PAPER_TYPE_BLINEDETECT->{
                sendCommand("BLINEDETECT [${config.height*12},${config.margin*12}]")
            }
        }
        sendCommand("CLS\r\n")
        try {
            sendBitmap(0, 0, bitmap)
        } catch (e: Exception) {
            return -1
        }
        sendCommand("PRINT 1\r\n")
        //回退40mm
//        sendCommand("BACKUP 216\r\n")
        //切刀
//        sendCommand("CUT\r\n")
        //送出40mm
//        sendCommand("FEED 216\r\n")
//        sendCommand("\r\n")
        LogUtils.file("打印结束")
        return 0
    }

    private fun openPort(usbManager: UsbManager, device: UsbDevice): Boolean {
        LogUtils.file("openPort")
        this.mUsbIntf = device.getInterface(0)
        mUsbendpoint = mUsbIntf!!.getEndpoint(0)
        mUsbConnection = usbManager.openDevice(device)
        val portStatus = mUsbConnection!!.claimInterface(this.mUsbIntf, true)

        for (i in 0 until mUsbIntf!!.endpointCount) {
            val end: UsbEndpoint = mUsbIntf!!.getEndpoint(i)
            if (end.direction == 128) {
                usbEndpointIn = end
            } else {
                usbEndpointOut = end
            }
        }

        try {
            //Thread.sleep(100L)
        } catch (var6: InterruptedException) {
            var6.printStackTrace()
        }
        LogUtils.file("portStatus=$portStatus")
        return portStatus
    }

    private fun closePort(): Boolean {
        if (mUsbConnection == null) {
            return false
        }

        runCatching {
            Thread.sleep(1300L)
        }.onFailure {
            it.printStackTrace()
        }

        return try {
            mUsbConnection?.apply {
                close()
                releaseInterface(mUsbIntf)
            }
            mUsbConnection = null
            mUsbendpoint = null
            mUsbManager = null
            mUsbDevice = null
            mUsbIntf = null
            Log.d(TAG, "Device closed. ")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            false
        }
    }

    private fun sendCommand(printerCommand: String): Boolean {
        Log.d(TAG, "sendCommand: $printerCommand")
        LogUtils.file(printerCommand)
        if (mUsbConnection == null) {
            return false
        }

        runCatching {
//            Thread.sleep(100L)
            val command = printerCommand.toByteArray()
            mUsbConnection!!.bulkTransfer(mUsbendpoint, command, command.size, TIMEOUT)
        }.onFailure {
            it.printStackTrace()
        }

        try {
//            Thread.sleep(200L)
        } catch (var4: InterruptedException) {
        }

        return true
    }

    private fun sendCommandOld(printercommand: String): String? {
        return if (mUsbConnection == null) {
            "-1"
        } else {
            try {
                Thread.sleep(100L)
            } catch (var6: InterruptedException) {
            }
            val thread = Thread {
                val command = printercommand.toByteArray()
                mUsbConnection!!.bulkTransfer(
                    mUsbendpoint,
                    command,
                    command.size,
                    TIMEOUT
                )
            }
            thread.start()
            try {
                thread.join()
            } catch (var5: InterruptedException) {
                var5.printStackTrace()
            }
            try {
                Thread.sleep(100L)
            } catch (var4: InterruptedException) {
            }
            "1"
        }
    }

    // 将位图转换为打印机可识别的字节流格式
    private fun sendBitmap(xCoordinates: Int = 0, yCoordinates: Int = 0, bitmap: Bitmap) {
        val x = xCoordinates.toString()
        val y = yCoordinates.toString()

        // 计算位图每行所占用的字节数
        val widthBytes = (bitmap.width + 7) / 8

        val pictureWidth = widthBytes.toString()
        val pictureHeight = bitmap.height.toString()
        val mode = "0"

        val command = "BITMAP $x,$y,$pictureWidth,$pictureHeight,$mode,"
        val stream = ByteArray(widthBytes * bitmap.height)

        val width = bitmap.width
        val height = bitmap.height

        // 初始化 stream 数组
        for (i in stream.indices) {
            stream[i] = -1
        }
        // 遍历像素并修改 stream
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelColor = bitmap.getPixel(x, y)
                val colorR = Color.red(pixelColor)
                val colorG = Color.green(pixelColor)
                val colorB = Color.blue(pixelColor)
                val total = (colorR + colorG + colorB) / 3
                // 如果像素的平均亮度为 0，将对应的 stream 元素进行修改
                if (total == 0) {
                    // 计算在 stream 数组中的索引位置
                    val index = y * ((width + 7) / 8) + x / 8
                    // 计算要修改的 bit 位置
                    val bitPosition = 128 shr x % 8
                    // 使用异或操作修改 stream 的对应位
                    stream[index] =
                        (stream[index].toInt() xor bitPosition.toByte().toInt()).toByte()
                }
            }
        }


        // 输出调试日志
        LogUtils.file("sendBitmap: ----")
        // 发送打印指令和字节流数据到打印机
        //TscUSB.sendCommand("$command${byteArrayToHex(stream)}$\r\n")
        sendCommand(command)
        sendCommandLargeByte(stream)
        sendCommand("\r\n")
    }

    private fun sendCommandLargeByte(command: ByteArray): String {
        Log.d(TAG, "byte size= ${command.size}")
        LogUtils.file("byte size= ${command.size}")
        return if (mUsbConnection == null) {
            "-1"
        } else {
            try {
//                Thread.sleep(100L)
            } catch (var6: InterruptedException) {
                LogUtils.file(var6)
            }
            val thread = Thread {
                var offset = 0
                val totalSize = command.size
                while (offset<totalSize) {
                    val remainingSize = totalSize - offset
                    val bufferSize = minOf(remainingSize, MAX_USBFS_BUFFER_SIZE)
                    LogUtils.d("- remainingSize=${remainingSize} bufferSize =$bufferSize offset =$offset total=$totalSize")
                    val buffer = command.copyOfRange(offset, offset + bufferSize)
                    LogUtils.d("-- bufferSize=${buffer.size}  bulkTransfer offset=$offset, total=$totalSize")

                    LogUtils.d("--- bulkTransfer  buffer=${buffer}, bufferSize=${buffer.size}")
                    val transferred = mUsbConnection!!.bulkTransfer(mUsbendpoint, buffer,0, buffer.size, TIMEOUT)
                    offset += MAX_USBFS_BUFFER_SIZE
                    if (offset >= totalSize) {
                        val crlfByte = "\r\n".toByteArray()
                        mUsbConnection!!.bulkTransfer(mUsbendpoint, crlfByte, 0, crlfByte.size, TIMEOUT)
                        break
                    }
                }
            }
            thread.start()
            try {
                thread.join()
            } catch (var5: InterruptedException) {
                var5.printStackTrace()
            }
//            try {
//                Thread.sleep(500L)
//            } catch (var4: InterruptedException) {
//            }
            Log.d(TAG, "sendCommandLargeByte: end")
            "1"
        }
    }

    private fun Int.toPix(): Int {
        return (this * DPI).toInt()
//        return if (this > 10)
//            (this * 11.8).toInt()
//        else
//            this * DPI
    }

    override fun OpenDevice(
        DeviceID: Int,
        deviceFile: String?,
        szPort: String?,
        szParam: String?
    ): Int {
        Log.d(TAG, "OpenDevice: --")
        return 0
    }

    override fun CloseDevice(DeviceID: Int): Int {
        bitmapPrinter?.recycle()
        Log.d(TAG, "getStatus: ")
        return 0
    }

    override fun getStatus(): Int {
        Log.d(TAG, "getStatus: ")

        return 0
    }

    override fun setPageSize(format: Bundle?): Int {
        /**
         * format – 指定打印设置格式
         * pageW(int)：纸张宽度（毫米），不能大于门票纸，否则可能导致定位
         * 错误
         * pageH(int)：纸张高度（毫米），不能大于门票纸，否则可能导致定位错q
         * 误
         * direction(int)：打印起始坐标方向，0－出纸方向右下角为坐标原点，1
         * －出纸方向左上角为坐标原点
         * OffsetX(int)：左偏移量（毫米）,设置后，打印内容全部往左偏移指定位
         * 置
         * OffsetY(int)：上偏移量（毫米）,设置后，打印内容全部往下偏移指定位
         * 置
         */
        val pageW = format!!.getInt("pageW")
        val pageH = format.getInt("pageH")
        val direction = format.getInt("direction")
        val offsetX = format.getInt("OffsetX")
        val offsetY = format.getInt("OffsetY")
        Log.d(
            TAG,
            "setPageSize: pageW=$pageW pageH=$pageH direction=$direction OffsetX=$offsetX OffsetY=$offsetY"
        )
        LogUtils.file(" pageW=$pageW pageH=$pageH direction=$direction OffsetX=$offsetX OffsetY=$offsetY")
        bitmapPrinter = BitmapPrinterV3(config)
        bitmapPrinter?.setPageSize(
            pageW.toPix(),
            pageH.toPix(),
            direction,
            offsetX.toPix(),
            offsetY.toPix()
        )
        this.pageW = pageW
        this.pageH = pageH

        return 0
    }

    override fun startPrintDoc(): Int {
        LogUtils.file("startPrintDoc-----------")
        return 0
    }

    override fun addText(format: Bundle, text: String): Int {
        /**
         * fontName(Sting)：字体名称，安卓下使用的字体文件必须放在
         * asset\font 目录下，例如填： FZLTXHJW.TTF ， 则该字体文件存
         * 放在项目的 asset\font\FZLTXHJW.TTF
         * fontSize(int)：字体大小，一般设置为 10~16 大小
         * rotation(int)：旋转角度，0，90，180，270 四个角度
         * iLeft(int): 距离左边距离,单位 mm
         * iTop(int): 距离顶部距离,单位 mm
         * align(int)：对齐方式，默认左对齐，0:left, 1:center, 2:right
         * pageWidth(int)：文本打印宽度，单位:MM，如果内容超过宽度会自动
         * 换行
         */
        val fontName = format!!.getString("fontName")
        val fontSize = format!!.getInt("fontSize")
        val rotation = format!!.getInt("rotation")
        val iLeft = format!!.getInt("iLeft")
        val iTop = format!!.getInt("iTop")
        val align = format!!.getInt("align")
        val pageWidth = format!!.getInt("pageWidth")
        Log.d(
            TAG,
            "addText: text=$text fontSize=$fontSize rotation=$rotation iLeft=$iLeft iTop=$iTop align=$align pageWidth=$pageWidth"
        )
        bitmapPrinter?.addText(
            text,
            fontSize,
            rotation,
            iLeft.toPix(),
            iTop.toPix(),
            align,
            pageWidth.toPix()
        )
        LogUtils.file("text=$text fontSize=$fontSize rotation=$rotation iLeft=$iLeft iTop=$iTop align=$align pageWidth=$pageWidth")
        return 0
    }

    override fun addQrCode(format: Bundle?, qrCode: String?): Int {
        /**
         * iLeft(int): 距离左边距离,单位 mm
         * iTop(int): 距离顶部距离,单位 mm
         * expectedHeight(int) - 期望高度，单位 mm
         * qrCode – 二维码内容
         */
        val iLeft = format!!.getInt("iLeft")
        val iTop = format!!.getInt("iTop") //7
        val expectedHeight = format!!.getInt("expectedHeight") //16
        //宽 1~16
        //宽 1~16
        Log.d(TAG, "addQrCode: iLeft=$iLeft iTop=$iTop expectedHeight=$expectedHeight")
        LogUtils.file("iLeft=$iLeft iTop=$iTop expectedHeight=$expectedHeight,qrCode=$qrCode")
        bitmapPrinter?.addQrCode(iLeft.toPix(), iTop.toPix(), expectedHeight.toPix(), qrCode!!)
        return 0
    }

    override fun addImage(format: Bundle?, imageData: String?): Int {
        Log.d(TAG, "addImage: ")
        LogUtils.file("$format,$imageData")
        return 0
    }

    override fun endPrintDoc(): Int {
        var status = -1
        Log.d(TAG, "endPrintDoc: ----start")
        LogUtils.file("endPrintDoc ----start")
        var bitmap = bitmapPrinter?.drawEnd()
        bitmap?.let {
            LogUtils.d("等比旋转角${config.rotation}")
            LogUtils.file("等比旋转角${config.rotation}")
            bitmap= bitmapPrinter?.rotateBitmap(it, config.rotation.toFloat())
        }

        bitmap?.let {
            status = printerBitmap(it)
        }
        Log.d(TAG, "endPrintDoc: ----end")
        bitmap?.recycle()
        bitmapPrinter?.recycle()
        bitmapPrinter == null
        LogUtils.file("endPrintDoc----------------------------")
        return status
    }
}