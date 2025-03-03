package com.icbc.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.icbc.ui.IDCardTestActivity
import com.icbc.ui.PrinterTestActivity
import com.icbc.ui.ScannerTestActivity
import com.icbc.ui.compose.ui.theme.ICBCAIDLTheme
import com.utils.DevicesInfo
import com.utils.TTYUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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
    val content = LocalContext.current
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
        version = DevicesInfo.getAppVersion(content) ?: "NULL"
        ttys = TTYUtils.getSerialPort().second.toList()
        ConfigProvider.readConfig(content).firstOrNull()?.let {
            config = it
            configState = ConfigConverter.toUIState(it)
            LogUtils.d(config, configState)
        }
    }
    val handleUploadLog: () -> Unit = {
        lifecycleOwner.lifecycleScope.launch {
            ToastUtils.showLong("上传日志,请等待")
        }
    }

    val handleSave: () -> Unit = {
        lifecycleOwner.lifecycleScope.launch {
            config = ConfigConverter.toConfig(configState)
            ConfigProvider.saveConfig(content, config)
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
                        Button(onClick = { IDCardTestActivity.start(content) }) {
                            Text(text = "身份证测试")
                        }
                        Button(onClick = { ScannerTestActivity.start(content) }) {
                            Text(text = "扫码测试")
                        }
                        Button(onClick = {
                            PrinterTestActivity.start(
                                content,
                                PRINT_TSC310E
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