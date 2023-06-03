package com.icbc.selfserviceticketing.deviceservice.printer

import android.graphics.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*

/**
 * TODO
 * @date 2022/6/13
 * @author 锅得铁
 * @since v1.0
 */
class QRCodeUtils {
    fun generateImage(text: String, size: Int): Bitmap? {
        var qrImage: Bitmap? = null
        if (text.trim { it <= ' ' }.isEmpty()) {
            return null
        }
        val hintMap: MutableMap<EncodeHintType, Any?> = EnumMap(
            EncodeHintType::class.java
        )
        hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hintMap[EncodeHintType.MARGIN] = 1
        val qrCodeWriter = QRCodeWriter()
        try {
            val byteMatrix = qrCodeWriter.encode(
                text, BarcodeFormat.QR_CODE, size,
                size, hintMap
            )
            val height = byteMatrix.height
            val width = byteMatrix.width
            qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrImage.setPixel(x, y, if (byteMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return qrImage
    }

     fun createCustomBitmap(qrCodeBitmap: Bitmap, textContent: String): Bitmap? {
        // 创建一个空的 Bitmap
        val width = 576f
        val height = 900f
        val printerBitmap =
            Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(printerBitmap)
         canvas.drawColor(Color.WHITE)

        // 绘制文字
        val mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        // 绘制二维码
        val dstRect = Rect(200, 200, 200, 400)
        val qrLeft = 20f
        val qrTop = 20f
        canvas.drawBitmap(qrCodeBitmap, qrLeft, qrTop, null)
        mPaint.color = Color.BLACK
        mPaint.textSize = 18f
        mPaint.style = Paint.Style.FILL
        canvas.drawText(textContent, 200f, 400f, mPaint)
        return printerBitmap
    }
}