package com.icbc.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.ConfigProvider
import com.icbc.selfserviceticketing.deviceservice.ID_180
import com.icbc.selfserviceticketing.deviceservice.ID_M40
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINE
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINEDETECT
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP
import com.icbc.selfserviceticketing.deviceservice.PRINT_CSN
import com.icbc.selfserviceticketing.deviceservice.PRINT_TSC310E
import com.icbc.selfserviceticketing.deviceservice.R
import com.icbc.selfserviceticketing.deviceservice.databinding.ActivityMainBinding
import com.icbc.selfserviceticketing.deviceservice.utils.LogUtilsUpload
import com.lxj.xpopup.XPopup
import com.utils.SystemUtil
import com.utils.TTYUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.EnumMap


class MainActivity : AppCompatActivity() {

    private var config = Config()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnReadIDCard.setOnClickListener {
            val intent = Intent(this, IDCardTestActivity::class.java)
            startActivity(intent)
        }
        binding.btnScanner.setOnClickListener {
            val intent = Intent(this, ScannerTestActivity::class.java)
            startActivity(intent)
        }
        binding.btnPrinter.setOnClickListener {
            PrinterTestActivity.start(this)
        }
        binding.cbEnableScannerTTY.setOnCheckedChangeListener{v,b->
            config.enableScannerSuperLeadSerialPortMode  = b
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            runCatching {
                val storeConfig = ConfigProvider.readConfig(applicationContext).firstOrNull()
                storeConfig?.let {
                    config = storeConfig
                }
            }
            setInfo()
            uploadLog()
            idSwitch()
            printerSwitch()
        }
    }

    private fun printerSwitch() {
        when (config.printerType) {
            PRINT_CSN -> {
                binding.rg2.check(R.id.rbCSN)
            }

            PRINT_TSC310E -> {
                binding.rg2.check(R.id.rbTSC310E)
            }
        }
        binding.rg2.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val text = radioButton.text
            if (text.equals("CSN")) {
                config.printerType = PRINT_CSN
            }
            if (text.equals("TSC310E")) {
                config.printerType = PRINT_TSC310E
            }
            ToastUtils.showLong(text)
        }
        binding.csnPrinterDevPort.text = config.csnDevPort
        binding.csnPrinterDevPort.setOnClickListener {
            val data = TTYUtils.getSerialPort().second
            XPopup.Builder(this)
                .asCenterList("选择串口", data) { p, t ->
                    binding.csnPrinterDevPort.text = t
                    config.csnDevPort = t
                }.show()
        }
    }

    private fun idSwitch() {
        lifecycleScope.launch {
            when (config.idCardType) {
                ID_180 -> {
                    this@MainActivity.binding.rg.check(R.id.rdID180)
                }

                ID_M40 -> {
                    this@MainActivity.binding.rg.check(R.id.rdIDM40)
                }
            }
        }
        binding.rg.setOnCheckedChangeListener { group, checkedId ->
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
       binding.uploadLog.setOnClickListener {

        }
    }

    private fun setInfo() {
        val deviceSerial = SystemUtil.getSN()
        val address = SystemUtil.getMacAddress()
        binding.tvDeviceInfo.text = "序列号：${deviceSerial}\nMAC地址:${address}\n"
        binding.img.setImageBitmap(generateQrImage("${deviceSerial}\n${address}", 280))
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                ConfigProvider.saveConfig(applicationContext, config)
                finish()
            }
        }
        lifecycleScope.launch {
            when (config.paperType) {
                PAPER_TYPE_CAP -> {
                    binding.rbPaperTBZ.isChecked = true
                }
                PAPER_TYPE_BLINE -> {
                    binding.rbPaperHBZ.isChecked = true
                }
                PAPER_TYPE_BLINEDETECT -> {
                    binding.rbPaperLXZ.isChecked = true
                }
            }
            binding.cbBorder.isChecked = config.enableBorder
            binding.editRotation.setText("${config.rotation}")
            binding.editWidth.setText("${config.width}")
            binding.editHeight.setText("${config.height}")
            binding.editMargin.setText("${config.margin}")
            binding.cbEnableScannerTTY.isChecked = config.enableScannerSuperLeadSerialPortMode
        }

        binding.rgPaper.setOnCheckedChangeListener { ck, b ->
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

        binding.cbBorder.setOnCheckedChangeListener { ck, b ->
            config.enableBorder = b
            ToastUtils.showLong("$b")
        }

        binding.editRotation.addTextChangedListener(object : TextWatcher {
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
        binding.editWidth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                runCatching {
                    if (p0.isEmpty())
                        return
                    val width: String = p0.toString()
                    config.width = width.toFloat()
                }
            }
        })
        binding.editHeight.addTextChangedListener(object : TextWatcher {
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
        binding.editMargin.addTextChangedListener(object : TextWatcher {
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


}