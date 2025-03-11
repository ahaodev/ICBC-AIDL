package com.icbc.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINE
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_BLINEDETECT
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_HOT
import com.icbc.selfserviceticketing.deviceservice.PRINT_CSN
import com.icbc.selfserviceticketing.deviceservice.PRINT_T321OR331
import com.icbc.selfserviceticketing.deviceservice.PRINT_TSC310E

@Composable
fun PrinterConfigPage(
    printerConfig: PrinterConfigUIState,
    ttys: List<String>,
    onConfigChange: (newConfig: PrinterConfigUIState) -> Unit
) {
    var expandedPrinterTypeSelector by remember { mutableStateOf(false) }
    var expandedPaperSelector by remember { mutableStateOf(false) }
    var expandedTTYSelector by remember { mutableStateOf(false) }

    val printerTypeOptions = listOf(PRINT_CSN, PRINT_TSC310E, PRINT_T321OR331)
    val paperTypeOptions = listOf(PAPER_TYPE_CAP, PAPER_TYPE_BLINE, PAPER_TYPE_BLINEDETECT,
        PAPER_TYPE_HOT)

    var currentPaperWidth by remember { mutableStateOf(TextFieldValue(printerConfig.paperWidth)) }
    var currentPaperHeight by remember { mutableStateOf(TextFieldValue(printerConfig.paperHeight)) }
    var currentPaperPadding by remember { mutableStateOf(TextFieldValue(printerConfig.paperPadding)) }
    var currentPaperAngle by remember { mutableStateOf(TextFieldValue(printerConfig.rotationAngle)) }

    // 同步外部状态变化
    LaunchedEffect(printerConfig) {
        currentPaperWidth = TextFieldValue(printerConfig.paperWidth, selection = currentPaperWidth.selection)
        currentPaperHeight = TextFieldValue(printerConfig.paperHeight, selection = currentPaperHeight.selection)
        currentPaperPadding = TextFieldValue(printerConfig.paperPadding, selection = currentPaperPadding.selection)
        currentPaperAngle = TextFieldValue(printerConfig.rotationAngle, selection = currentPaperAngle.selection)
    }

    fun handleConfigChange(
        printType: String = printerConfig.printType,
        printerTTY: String = printerConfig.printerTTY,
        paperType: String = printerConfig.paperType,
        paperWidth: String = currentPaperWidth.text,
        paperHeight: String = currentPaperHeight.text,
        paperPadding: String = currentPaperPadding.text,
        rotationAngle: String = currentPaperAngle.text,
        enableBorder: Boolean = printerConfig.enableBorder
    ) {
        onConfigChange(
            PrinterConfigUIState(
                printType = printType,
                printerTTY = printerTTY,
                paperType = paperType,
                paperWidth = paperWidth,
                paperHeight = paperHeight,
                paperPadding = paperPadding,
                rotationAngle = rotationAngle,
                enableBorder = enableBorder
            )
        )
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "选择打印机:")
                Box {
                    TextButton(onClick = { expandedPrinterTypeSelector = true }) {
                        Text(printerConfig.printType)
                    }
                    DropdownMenu(
                        expanded = expandedPrinterTypeSelector,
                        onDismissRequest = { expandedPrinterTypeSelector = false }
                    ) {
                        printerTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    handleConfigChange(printType = option)
                                    expandedPrinterTypeSelector = false
                                }
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "选择串口号:")
                    Box {
                        TextButton(onClick = { expandedTTYSelector = true }) {
                            Text(printerConfig.printerTTY)
                        }
                        DropdownMenu(
                            expanded = expandedTTYSelector,
                            onDismissRequest = { expandedTTYSelector = false }
                        ) {
                            ttys.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        handleConfigChange(printerTTY = option)
                                        expandedTTYSelector = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "纸张类型:")
                Box {
                    TextButton(onClick = { expandedPaperSelector = true }) {
                        Text(printerConfig.paperType)
                    }
                    DropdownMenu(
                        expanded = expandedPaperSelector,
                        onDismissRequest = { expandedPaperSelector = false }
                    ) {
                        paperTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    handleConfigChange(paperType = option)
                                    expandedPaperSelector = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = currentPaperWidth,
                    onValueChange = { newValue ->
                        if (newValue.text.all { newValue.text.matches(Regex("^\\d*\\.?\\d*$"))|| it == ' ' } || newValue.text.isEmpty()) {
                            currentPaperWidth = newValue
                            handleConfigChange(paperWidth = newValue.text)
                        }
                    },
                    label = { Text("纸宽度") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = currentPaperHeight,
                    onValueChange = { newValue ->
                        if (newValue.text.all { newValue.text.matches(Regex("^\\d*\\.?\\d*$"))|| it == ' ' } || newValue.text.isEmpty()) {
                            currentPaperHeight = newValue
                            handleConfigChange(paperHeight = newValue.text)
                        }
                    },
                    label = { Text("纸高度") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = currentPaperPadding,
                    onValueChange = { newValue ->
                        if (newValue.text.all { newValue.text.matches(Regex("^\\d*\\.?\\d*$"))|| it == ' ' } || newValue.text.isEmpty()) {
                            currentPaperPadding = newValue
                            handleConfigChange(paperPadding = newValue.text)
                        }
                    },
                    label = { Text("纸间隙") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = currentPaperAngle,
                    onValueChange = { newValue ->
                        if (newValue.text.all { it.isDigit() || it == ' ' } || newValue.text.isEmpty()) {
                            currentPaperAngle = newValue
                            handleConfigChange(rotationAngle = newValue.text)
                        }
                    },
                    label = { Text("版面旋转角") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("打印调试边框")
                Checkbox(
                    checked = printerConfig.enableBorder,
                    onCheckedChange = { handleConfigChange(enableBorder = it) }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun PrinterPreview() {
    PrinterConfigPage(
        PrinterConfigUIState(),
        listOf("ttyS0", "ttyS1", "ttyS2"),
        onConfigChange = { newValue -> })
}