package com.icbc.ui.compose

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.ConfigProvider
import com.icbc.selfserviceticketing.deviceservice.PRINT_TSC310E
import com.icbc.selfserviceticketing.deviceservice.printer.QRCodeUtils
import com.icbc.selfserviceticketing.deviceservice.utils.LogUtilsUpload
import com.icbc.ui.IDCardTestActivity
import com.icbc.ui.PrinterTestActivity
import com.icbc.ui.ScannerTestActivity
import com.icbc.ui.compose.ui.theme.ICBCAIDLTheme
import com.lxj.xpopup.XPopup
import com.utils.DevicesInfo
import com.utils.TTYUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICBCAIDLTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    APP(
                        name = "ICBC-AIDL",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private val qrCodeUtils = QRCodeUtils()

@Composable
fun APP(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    var sn by remember { mutableStateOf("NULL") }
    var mac by remember { mutableStateOf("NULL") }
    var version by remember { mutableStateOf("NULL") }
    var config by remember { mutableStateOf(Config()) }
    var configState by remember { mutableStateOf(ConfigUIState()) }
    var ttys by remember { mutableStateOf(listOf("ttyS0", "ttyS1", "ttyS2")) }
    LaunchedEffect(configState) {
        sn = DevicesInfo.getSerialNumber()
        mac = DeviceUtils.getMacAddress()
        version = DevicesInfo.getAppVersion(context) ?: "NULL"
        ttys = TTYUtils.getSerialPort().second.toList()
        ConfigProvider.readConfig(context).firstOrNull()?.let {
            config = it
            configState = ConfigConverter.toUIState(it)
            LogUtils.d(config, configState)
        }
    }
    val handleUploadLog: () -> Unit = {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            ToastUtils.showLong("上传日志,请等待")
            var sbf = StringBuffer()
            LogUtilsUpload().uploadLogs {
                try {
                    sbf.append(JSONObject(it).getJSONObject("data").getString("url")).append("\n")
                } catch (e: java.lang.Exception) {
                    LogUtils.file(e)
                    sbf.append("上传日志\n $it")
                }
            }
            with(Dispatchers.Main){
                XPopup.Builder(context)
                    .asConfirm("已上传", sbf.toString()) {
                    }
                    .show()
//                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//                builder.setTitle("提示")
//                builder.setMessage(sbf.toString())
//                builder.setPositiveButton(
//                    "关闭"
//                ) { dialog, which -> // 点击关闭按钮后，关闭对话框
//                    dialog.dismiss()
//                }
//                val dialog: AlertDialog = builder.create()
//                dialog.show()
            }
        }
    }

    val handleSave: () -> Unit = {
        lifecycleOwner.lifecycleScope.launch {
            config = ConfigConverter.toConfig(configState)
            ConfigProvider.saveConfig(context, config)
            ToastUtils.showLong("保存成功")
            (context as? Activity)?.finish() // 结束当前 Activity
        }
    }

    Column(Modifier.padding(16.dp)) {
        Row {
            qrCodeUtils.generateImage("$sn | $mac", 180)?.let {
                Image(
                    bitmap = it.asImageBitmap(), contentDescription = "QRCode"
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "SN: $sn")
                Text(text = "MAC: $mac")
                Text(text = "Version: $version")
                Text(text = config.toString())
            }
        }
        ScannerConfigPage(configState.scanner, ttys) {
            configState = configState.copy(scanner = it)
            handleSave.invoke()

        }
        IDCardConfigPage(configState.idCard, ttys) {
            configState = configState.copy(idCard = it)
            handleSave.invoke()
        }
        PrinterConfigPage(configState.printer, ttys) { newConfig ->
            configState = configState.copy(printer = newConfig)
            handleSave.invoke()
        }
        Column {
            Card(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row {
                        Button(onClick = { IDCardTestActivity.start(context) }) {
                            Text(text = "身份证测试")
                        }
                        Button(onClick = { ScannerTestActivity.start(context) }) {
                            Text(text = "扫码测试")
                        }
                        Button(onClick = {
                            PrinterTestActivity.start(
                                context,
                                config.printerType
                            )
                        }) {
                            Text(text = "打印测试")
                        }
                    }
                }

            }

        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp), onClick = handleUploadLog
        ) {
            Text(text = "上传日志")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp), onClick = handleSave
        ) {
            Text(text = "保存参数")
        }
    }
}


@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun GreetingPreview() {
    ICBCAIDLTheme {
        APP("Android")
    }
}