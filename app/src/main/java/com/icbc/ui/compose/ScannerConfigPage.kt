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

@Composable
fun ScannerConfigPage(
    scanner: ScannerConfigUIState,
    ttys: List<String>,
    onScannerConfigChange: (Boolean, String) -> Unit
) {
    var enableUSB2SerialPort by remember { mutableStateOf(scanner.enableScannerSuperLeadSerialPortMode) }
    var expandedTTYSelector by remember { mutableStateOf(false) }
    var scannerTTYSelected by remember { mutableStateOf(scanner.serialPort) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "扫码设定:")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    "启用USB转串口模式(CSN) "
                )
                Checkbox(
                    checked = enableUSB2SerialPort,
                    onCheckedChange = {
                        enableUSB2SerialPort = it
                        onScannerConfigChange(
                            enableUSB2SerialPort,
                            scannerTTYSelected
                        )
                    }
                )

                if (enableUSB2SerialPort) {
                    Text("选择串口号:")
                    Box {
                        TextButton(onClick = { expandedTTYSelector = true }) {
                            Text(scannerTTYSelected)
                        }
                        DropdownMenu(
                            expanded = expandedTTYSelector,
                            onDismissRequest = {
                                expandedTTYSelector = false
                            }
                        ) {
                            ttys.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        scannerTTYSelected = option
                                        expandedTTYSelector = false
                                        onScannerConfigChange(
                                            enableUSB2SerialPort,
                                            scannerTTYSelected
                                        )
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
    ScannerConfigPage(ScannerConfigUIState(), listOf("ttyS0", "ttyS1", "ttyS2")) { _, _ ->
    }
}