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
import com.icbc.selfserviceticketing.deviceservice.PRINTER_TSC310E
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
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    var configState by remember { mutableStateOf(ConfigUIState()) }
    var sn by remember { mutableStateOf("NULL") }
    var mac by remember { mutableStateOf("NULL") }
    var version by remember { mutableStateOf("NULL") }
    val content = LocalContext.current
    var ttys by remember { mutableStateOf(listOf("ttyS0", "ttyS1", "ttyS2")) }
    var config by remember { mutableStateOf(Config()) }
    LaunchedEffect(lifecycleOwner) {
        sn = DevicesInfo.getSerialNumber()
        mac = DeviceUtils.getMacAddress()
        version = DevicesInfo.getAppVersion(content) ?: "NULL"
        ttys = TTYUtils.getSerialPort().second.toList()
        val storeConfig = ConfigProvider.readConfig(content).firstOrNull()
        storeConfig?.let {
            config = storeConfig
        }
    }
    val handleUploadLog: () -> Unit = {
        lifecycleOwner.lifecycleScope.launch {

        }
    }

    val handleSave: () -> Unit = {
        lifecycleOwner.lifecycleScope.launch {
            ToastUtils.showLong("handleSave")
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
                Text(text = "$config")
            }
        }
        ScannerConfigPage(configState.scanner, ttys) { enableSerialPort, serialPort ->
            LogUtils.d(enableSerialPort, serialPort)
            configState = configState.copy(
                scanner = ScannerConfigUIState(
                    enableScannerSuperLeadSerialPortMode = enableSerialPort,
                    serialPort = serialPort
                )
            )
        }
        IDCardDropdownMenu(configState.idCard, ttys) {
            configState = configState.copy(
                idCard = IDCardConfigUIState(
                    idCardType = it
                )
            )
        }
        PrinterDropdownMenu(configState.printer, ttys, onPrinterChange = { type, serialPort ->
            configState = configState.copy(
                printer = PrinterConfigUIState(
                    printType = type,
                    printerTTY = serialPort
                )
            )

        }, onPaperChange = { paperType, paperWidth, paperHeight, paperPadding, paperAngle ->
            configState = configState.copy(
                printer = PrinterConfigUIState(
                    paperType = paperType,
                    paperWidth = paperWidth,
                    paperHeight = paperHeight,
                    paperPadding = paperPadding,
                    rotationAngle = paperAngle.toInt()
                )
            )
        })
        val content1 = LocalContext.current
        Column {
            Card(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row {
                        Button(onClick = { IDCardTestActivity.start(content1) }) {
                            Text(text = "身份证测试")
                        }
                        Button(onClick = { ScannerTestActivity.start(content1) }) {
                            Text(text = "扫码测试")
                        }
                        Button(onClick = { PrinterTestActivity.start(content1, PRINTER_TSC310E) }) {
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