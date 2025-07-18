package com.icbc.selfserviceticketing.deviceservice.printer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PorterDuff
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.icbc.selfserviceticketing.deviceservice.Config
import java.util.*
import kotlin.math.roundToInt

/**
openDevice: device =  VID:0FE6 PID:811EVendorId=4070ProductId=33054
OnOpen: 0
setPageSize: pageW=72
setPageSize: pageH=120
setPageSize: direction=0
setPageSize: OffsetX=0
setPageSize: OffsetY=

addText: text=票券名称 fontSize=20 rotation=0 iLeft=0 iTop=0 align=1 pageWidth=71
addQrCode: iLeft=6 iTop=6 expectedHeight=63 qrCode=27636652205751<MjAwMDAwMTkyNDIwMjMtMDYtMDIyMDIzLTA2LTAy>
addText: text=证件类型 fontSize=14 rotation=0 iLeft=20 iTop=73 align=1 pageWidth=30
addText: text=有效期 fontSize=14 rotation=0 iLeft=20 iTop=81 align=1 pageWidth=30
addText: text=固定文字 fontSize=14 rotation=0 iLeft=20 iTop=90 align=1 pageWidth=30
addText: text=hao88打印测试票 fontSize=20 rotation=0 iLeft=0 iTop=6 align=0 pageWidth=71
addText: text= fontSize=14 rotation=0 iLeft=20 iTop=79 align=0 pageWidth=71
addText: text=2023-06-02 fontSize=14 rotation=0 iLeft=20 iTop=87 align=0 pageWidth=71
endPrintDoc: 结束打印任务
 */
class BitmapPrinterV3(val config: Config) {

    companion object {
        const val TAG = "BitmapPrinterV3"
        private val dpi = 8
        private val TITLE_FIELDS = listOf(
            "票券名称", "票券编号", "姓名",
            "证件类型","证件号码","有效期",
            "订单号","固定文字","使用人数",
            "票型", "子票信息","销售日期",
            "销售时间","打印日期","打印时间",
            "销售时间","打印日期","打印时间",
            "票价","区域","座位",
            "演出日期","打印人员","出行时段",
            "接待单位","可提前入场...","场次",
            "场地","分销商名称","销售渠道",
            "可用票数","购票人姓名","购票人手机",
            "售价","下单人"
        )
    }
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas

    private var mPaint: Paint = Paint()

    private var textPaint: TextPaint = TextPaint().apply {
        style = Paint.Style.STROKE
    }

    fun setPageSize(
        pageW: Int, pageH: Int, direction: Int = 0, OffsetX: Int = 0, OffsetY: Int = 0
    ) {
        val (bitmap, canvas) = basicBitmap(pageW, pageH)
        this.bitmap = bitmap
        this.canvas = canvas
    }


    /**
    fontName(Sting)：字体名称，安卓下使用的字体文件必须放在
    asset\font 目录下，例如填： FZLTXHJW.TTF ， 则该字体文件存放在项目的 asset\font\FZLTXHJW.TTF
    fontSize(int)：字体大小，一般设置为 10~16 大小
    rotation(int)：旋转角度，0，90，180，270 四个角度
    iLeft(int): 距离左边距离,单位 mm
    iTop(int): 距离顶部距离,单位 mm
    align(int)：对齐方式，默认左对齐，0:left, 1:center, 2:right
    pageWidth(int)：文本打印宽度，单位:MM，如果内容超过宽度会自动换行
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun addText(
        text: String,
        fontSize: Int,
        rotation: Int,
        iLeft: Int,
        iTop: Int,
        align: Int,
        pageWidth: Int
    ) {
        var printerText =text
        for (condition in TITLE_FIELDS) {
            if (text.contains(condition)) {
                if (!text.contains(":")){
                    printerText = "$text:"
                    break
                }
            }
        }
        var alignment = when (align) {
            0 -> {
                Layout.Alignment.ALIGN_NORMAL
            }

            1 -> {
                Layout.Alignment.ALIGN_CENTER
            }

            2 -> {
                Layout.Alignment.ALIGN_OPPOSITE
            }

            else -> {
                Layout.Alignment.ALIGN_NORMAL
            }
        }
        var x = iLeft
//        if (alignment == Layout.Alignment.ALIGN_CENTER) {
//            x += (bitmap.width - pageWidth) / 2
//        }
        var y = iTop
        textPaint.textSize = fontSize.toFloat()
        val measureTextWidth = textPaint.measureText(printerText)//测量宽
        Log.d(TAG, "measureTextWidth: $measureTextWidth")
        var layoutWidth: Int =
            if (pageWidth > measureTextWidth) pageWidth else measureTextWidth.roundToInt()//选择大的

//        if (layoutWidth > (72 * dpi)) {
//            layoutWidth = 72 * dpi - iLeft
//        }
        if ("票券名称" == text)
            layoutWidth += 4

        drawText(printerText, fontSize, x.toFloat(), y.toFloat(), layoutWidth, alignment,rotation)
    }

    private fun drawText(
        text: String,
        fontSize: Int,
        x: Float,
        y: Float,
        textWidth: Int,
        align: Layout.Alignment,
        rotation: Int
    ) {
        textPaint.textSize = fontSize.toFloat()
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL
        var autoText = text
        if (text.contains(";")){
            autoText= text.replace(";", ";\n")
        }
        LogUtils.file(" ; 换行",autoText)
        val staticLayout = StaticLayout.Builder.obtain(
            autoText, 0, autoText.length, textPaint, textWidth
        )
            .setAlignment(align)
            .build()

        // 创建一个子 Canvas 记录旋转操作
        val picture = Picture()
        val subCanvas = picture.beginRecording(textWidth, staticLayout.height)

        // 在子 Canvas 上绘制文本
        subCanvas.save()
        subCanvas.translate(0f, 0f)
        staticLayout.draw(subCanvas)
        subCanvas.restore()

        picture.endRecording()

        // 在主 Canvas 上旋转并绘制子 Canvas 的内容
        canvas.save()
        canvas.translate(x, y)
        canvas.rotate(rotation.toFloat(), 0f, 0f)
        canvas.drawPicture(picture)
        canvas.restore()

        Log.d(TAG, "drawText: text=$text x=$x y=$y textWidth=$textWidth rotation=$rotation")
    }

    /**
    iLeft(int): 距离左边距离,单位 mm
    iTop(int): 距离顶部距离,单位 mm
    expectedHeight(int) - 期望高度，单位 mm
    qrCode – 二维码内容
     */
    fun addQrCode(
        iLeft: Int,
        iTop: Int,
        expectedHeight: Int,
        qrCode: String = "27636652205751<MjAwMDAwMTkyNDIwMjMtMDYtMDIyMDIzLTA2LTAy>"
    ) {
        drawQrCode(qrCode, expectedHeight, iLeft.toFloat(), iTop.toFloat())
    }

    private fun drawQrCode(context: String, size: Int, x: Float, y: Float) {
        val bitmap = generateQrBitmap(text = context, size)
        if (null != bitmap) {
            Log.d(TAG, "drawQrCode: x=$x y=$y")
            canvas.drawBitmap(bitmap, x, y, null)
        }
    }

    private fun drawPadding(left :Float=8f, top :Float=8f, right :Float=8f, bottom:Float=8f){
        mPaint.style = Paint.Style.STROKE
        canvas.drawRect(left, top, bitmap.width - right, bitmap.height - bottom, mPaint)
    }
    fun drawEnd(): Bitmap {
        //canvas.restore()
        if (config.enableBorder){
            drawPadding()
        }
        return bitmap
    }

    fun recycle() = run {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // 使用透明色清空Canvas
        bitmap.recycle()
    }

    private fun generateQrBitmap(text: String, size: Int): Bitmap? {
        Log.d(TAG, "generateQrImage: text=$text size=$size")
        var qrImage: Bitmap? = null
        if (text.trim().isEmpty()) return null

        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 1)
        }
        try {
            val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val height = matrix.height
            val width = matrix.width
            qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrImage.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
            LogUtils.file(e)
        }
        return qrImage
    }


    private fun basicBitmap(width: Int, height: Int): Pair<Bitmap, Canvas> {
        Log.d(TAG, "basicBitmap: width=$width height=$height")
        LogUtils.file("width=${width},height=${height}")
        val printerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(printerBitmap)
        canvas.drawColor(Color.WHITE)
        return Pair(printerBitmap, canvas)
    }
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun Int.toPix(): Int {
        return (this * dpi).toInt()
//        return if (this > 10)
//            (this * 11.8).toInt()
//        else
//            this * DPI
    }
}