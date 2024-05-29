package com.icbc.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.icbc.selfserviceticketing.deviceservice.BuildConfig
import com.icbc.selfserviceticketing.deviceservice.Contains
import com.icbc.selfserviceticketing.deviceservice.DataStoreManager
import com.icbc.selfserviceticketing.deviceservice.R
import com.icbc.selfserviceticketing.deviceservice.printer.BitmapPrinter
import com.icbc.selfserviceticketing.deviceservice.utils.LogUtilsUpload
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.EnumMap


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setInfo()
        uploadLog()
    }

    private fun uploadLog() {
        findViewById<Button>(R.id.uploadLog).setOnClickListener {
            Thread {
                var sbf = StringBuffer()
                LogUtilsUpload().uploadLogs {
                    try {
                        sbf.append("\n")
                            .append(JSONObject(it).getJSONObject("data").getString("url"))
                    } catch (e: java.lang.Exception) {
                        LogUtils.file(e)
                        sbf.append("上传日志\n $it")
                    }
                }
                runOnUiThread {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("提示")
                    builder.setMessage(sbf.toString())
                    builder.setPositiveButton(
                        "关闭"
                    ) { dialog, which -> // 点击关闭按钮后，关闭对话框
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }.start()
        }
    }

    private fun setInfo() {
        val deviceSerial = Build.SERIAL
        val address = getMacAddress()
        val info = "序列号：${deviceSerial}\nMAC地址:${address}"
        findViewById<TextView>(R.id.tvDeviceInfo).apply {
            text = info
        }
        findViewById<ImageView>(R.id.img).apply {
            setImageBitmap(generateQrImage(info, 300))
        }
        findViewById<TextView>(R.id.tvVersion).text =
            "${BuildConfig.FLAVOR}-${BuildConfig.VERSION_NAME}"
        val editRotation = findViewById<EditText>(R.id.editRotation)
        lifecycleScope.launch {
            Contains.Rotation=DataStoreManager.getRotation(this@MainActivity).first()
            editRotation.setText("${Contains.Rotation}")
        }
        editRotation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                val rotation: String = p0.toString()
                runCatching {
                    Contains.Rotation = rotation.toInt()
                    lifecycleScope.launch {
                        DataStoreManager.setRotation(this@MainActivity, Contains.Rotation)
                    }
                }
            }
        })
    }

    private fun generateQrImage(text: String, size: Int): Bitmap? {
        Log.d(BitmapPrinter.TAG, "generateQrImage: text=$text size=$size")
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
                text, BarcodeFormat.QR_CODE, size, size, hintMap
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

    private fun getMacAddress(): String? {
        try {
            val process = Runtime.getRuntime().exec("su -c cat /sys/class/net/wlan0/address")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val macAddress = reader.readLine()
            process.waitFor()
            reader.close()
            return macAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}