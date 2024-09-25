package com.icbc.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.icbc.selfserviceticketing.deviceservice.BuildConfig
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.ConfigProvider
import com.icbc.selfserviceticketing.deviceservice.ID_180
import com.icbc.selfserviceticketing.deviceservice.ID_M40
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINE
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINEDETECT
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP
import com.icbc.selfserviceticketing.deviceservice.PRINTER_CSN
import com.icbc.selfserviceticketing.deviceservice.PRINTER_TSC310E
import com.icbc.selfserviceticketing.deviceservice.R
import com.icbc.selfserviceticketing.deviceservice.utils.LogUtilsUpload
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.EnumMap


class MainActivity : AppCompatActivity() {
    private var config = Config()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            runCatching {
                config = ConfigProvider.readConfig(applicationContext).first()
            }
            setInfo()
            uploadLog()
            idSwitch()
            printerSwitch()
        }
        val btnReadIDCard: Button = findViewById(R.id.btnReadIDCard)
        btnReadIDCard.setOnClickListener {
            val intent = Intent(this, IDCardTestActivity::class.java)
            startActivity(intent)
        }
        val btnScannerTest: Button = findViewById(R.id.btnScanner)
        btnScannerTest.setOnClickListener {
            val intent = Intent(this, ScannerTestActivity::class.java)
            startActivity(intent)
        }
        val btnPrinterTest: Button = findViewById(R.id.btnPrinter)
        btnPrinterTest.setOnClickListener {
            PrinterTestActivity.start(this, config.printerType)
        }
    }

    private fun printerSwitch() {
        val radioGroup = findViewById<RadioGroup>(R.id.rg2)
        when (config.printerType) {
            PRINTER_CSN -> {
                radioGroup.check(R.id.rbCSN)
            }

            PRINTER_TSC310E -> {
                radioGroup.check(R.id.rbTSC310E)
            }
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val text = radioButton.text
            if (text.equals("CSN")) {
                config.printerType = PRINTER_CSN
            }
            if (text.equals("TSC310E")) {
                config.printerType = PRINTER_TSC310E
            }
            ToastUtils.showLong(text)
        }
    }

    private fun idSwitch() {
        val radioGroup = findViewById<RadioGroup>(R.id.rg)
        lifecycleScope.launch {
            when (config.idCardType) {
                ID_180 -> {
                    radioGroup.check(R.id.rdID180)
                }

                ID_M40 -> {
                    radioGroup.check(R.id.rdIDM40)
                }
            }
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val text = radioButton.text
            if (text.equals("id180")) {
                config.idCardType = ID_180
            }
            if (text.equals("idm40")) {
                config.idCardType = ID_M40
            }
            ToastUtils.showLong(text)
        }
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
        findViewById<TextView>(R.id.tvDeviceInfo).apply {
            text =
                "序列号：${deviceSerial}\nMAC地址:${address}\n版本号:${BuildConfig.VERSION_NAME}"
        }
        findViewById<ImageView>(R.id.img).apply {
            setImageBitmap(
                generateQrImage(
                    "${deviceSerial}\n${address}",
                    280
                )
            )
        }
        val rgPaper = findViewById<RadioGroup>(R.id.rgPaper)
        val checkBoxBorder = findViewById<CheckBox>(R.id.cbBorder)
        val editRotation = findViewById<EditText>(R.id.editRotation)
        val editWidth = findViewById<EditText>(R.id.editWidth)
        val editHeight = findViewById<EditText>(R.id.editHeight)
        val editMargin = findViewById<EditText>(R.id.editMargin)
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            lifecycleScope.launch {
                ConfigProvider.saveConfig(applicationContext, config)
                finish()
            }
        }
        lifecycleScope.launch {
            when (config.paperType) {
                PAPER_TYPE_CAP -> {
                    findViewById<RadioButton>(R.id.rbPaperTBZ).isChecked = true
                }

                PAPER_TYPE_BLINE -> {
                    findViewById<RadioButton>(R.id.rbPaperHBZ).isChecked = true
                }

                PAPER_TYPE_BLINEDETECT -> {
                    findViewById<RadioButton>(R.id.rbPaperLXZ).isChecked = true
                }
            }
            checkBoxBorder.isChecked = config.enableBorder
            editRotation.setText("${config.rotation}")
            editWidth.setText("${config.weight}")
            editHeight.setText("${config.height}")
            editMargin.setText("${config.margin}")
        }

        rgPaper.setOnCheckedChangeListener { ck, b ->
            when (ck.checkedRadioButtonId) {
                R.id.rbPaperTBZ -> {
                    ToastUtils.showLong("铜版纸")
                    config.paperType = PAPER_TYPE_CAP
                }

                R.id.rbPaperHBZ -> {
                    ToastUtils.showLong("黑标纸")
                    config.paperType = PAPER_TYPE_BLINE
                }

                R.id.rbPaperLXZ -> {
                    ToastUtils.showLong("连续纸")
                    config.paperType = PAPER_TYPE_BLINEDETECT
                }
            }
        }

        checkBoxBorder.setOnCheckedChangeListener { ck, b ->
            config.enableBorder = b
            ToastUtils.showLong("$b")
        }

        editRotation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                runCatching {
                    if (p0.isEmpty())
                        return
                    val rotation: String = p0.toString()
                    config.rotation = rotation.toInt()
                }

            }
        })
        editWidth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                runCatching {
                    if (p0.isEmpty())
                        return
                    val weight: String = p0.toString()
                    config.weight = weight.toFloat()
                }
            }
        })
        editHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                runCatching {
                    if (p0.isEmpty())
                        return
                    val height: String = p0.toString()
                    config.height = height.toFloat()
                }
            }
        })
        editMargin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                runCatching {
                    if (p0.isEmpty())
                        return
                    val margin: String = p0.toString()
                    config.margin = margin.toFloat()
                }
            }
        })

    }

    private fun generateQrImage(text: String, size: Int): Bitmap? {
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