package com.icbc.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.LogUtils

@Composable
fun ScannerConfigPage(
    scanner: ScannerConfigUIState,
    ttys: List<String>,
    onConfigChange: (newConfig: ScannerConfigUIState) -> Unit
) {
    LogUtils.d(scanner)
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        var expandedTTYSelector by remember { mutableStateOf(false) } // 只保留下拉菜单的展开状态

        fun handleConfigChange(
            enableUSB2SerialPort: Boolean = scanner.enableScannerSuperLeadSerialPortMode,
            serialPort: String = scanner.serialPort
        ) {
            onConfigChange(
                ScannerConfigUIState(
                    scannerType = scanner.scannerType,
                    serialPort = serialPort,
                    enableScannerSuperLeadSerialPortMode = enableUSB2SerialPort
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "扫码设定:")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("启用USB转串口模式(CSN) ")
                Checkbox(
                    checked = scanner.enableScannerSuperLeadSerialPortMode,
                    onCheckedChange = { handleConfigChange(enableUSB2SerialPort = it) }
                )

                if (scanner.enableScannerSuperLeadSerialPortMode) {
                    Text("选择串口号:")
                    Box {
                        TextButton(onClick = { expandedTTYSelector = true }) {
                            Text(scanner.serialPort) // 直接使用 scanner.serialPort
                        }
                        DropdownMenu(
                            expanded = expandedTTYSelector,
                            onDismissRequest = { expandedTTYSelector = false }
                        ) {
                            ttys.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {

                                        handleConfigChange(serialPort = option)
                                        expandedTTYSelector = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun ScannerPagePreview() {
    ScannerConfigPage(ScannerConfigUIState(), listOf("ttyS0", "ttyS1", "ttyS2")) { newConfig ->
    }
}