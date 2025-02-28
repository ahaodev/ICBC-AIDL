package com.icbc.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
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
fun PrinterDropdownMenu(
    printerConfig: PrinterConfigUIState,
    ttys: List<String>,
    onPrinterChange: (type: String, serialPort: String) -> Unit,
    onPaperChange: (
        paperType: String,
        paperWidth: String, paperHeight: String, paperPadding: String, paperAngle: String
    ) -> Unit
) {
    var expandedPrinterTypeSelector by remember { mutableStateOf(false) }
    var expandedPaperSelector by remember { mutableStateOf(false) }
    var expandedTTYSelector by remember { mutableStateOf(false) }

    var printerTypeSelected by remember { mutableStateOf(printerConfig.printType) }
    val printerTypeOptions = listOf(PRINT_CSN, PRINT_TSC310E, PRINT_T321OR331)


    var printerTTYSelected by remember { mutableStateOf(printerConfig.printerTTY) }

    val paperTypeOptions = listOf(PAPER_TYPE_TB, PAPER_TYPE_BLACK, PAPER_TYPE_AGAIN)
    var paperTypeSelected by remember { mutableStateOf(printerConfig.paperType) }


    var paperWidth by remember { mutableStateOf(printerConfig.paperWidth) }
    var paperHeight by remember { mutableStateOf(printerConfig.paperHeight) }
    var paperPadding by remember { mutableStateOf(printerConfig.paperPadding) }
    var paperAngle by remember { mutableStateOf("${printerConfig.rotationAngle}") }
    fun handlePrinterChange() {
        onPrinterChange(printerTypeSelected, printerTTYSelected)
    }

    fun handlePaperChange() {
        onPaperChange(
            paperTypeSelected,
            paperWidth,
            paperHeight,
            paperPadding,
            paperAngle
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
                        Text(printerTypeSelected)
                    }
                    DropdownMenu(
                        expanded = expandedPrinterTypeSelector,
                        onDismissRequest = { expandedPrinterTypeSelector = false }
                    ) {
                        printerTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    printerTypeSelected = option
                                    expandedPrinterTypeSelector = false
                                    handlePrinterChange()
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
                            Text(printerTTYSelected)
                        }
                        DropdownMenu(
                            expanded = expandedTTYSelector,
                            onDismissRequest = { expandedTTYSelector = false }
                        ) {
                            ttys.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        printerTTYSelected = option
                                        expandedTTYSelector = false
                                        handlePrinterChange()
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
                        Text(paperTypeSelected)
                    }
                    DropdownMenu(
                        expanded = expandedPaperSelector,
                        onDismissRequest = { expandedPaperSelector = false }
                    ) {
                        paperTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    paperTypeSelected = option
                                    expandedPaperSelector = false
                                    handlePaperChange()
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
                    value = paperWidth,
                    onValueChange = { newValue ->
                        paperWidth = newValue
                        handlePaperChange()
                    },
                    label = { Text("纸宽度") },
                )
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = paperHeight,
                    onValueChange = { newValue ->
                        paperHeight = newValue
                        handlePaperChange()
                    },
                    label = { Text("纸高度") },
                )
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = paperPadding,
                    onValueChange = { newValue ->
                        paperPadding = newValue
                        handlePaperChange()
                    },
                    label = { Text("纸间隙") },
                )
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = paperAngle,
                    onValueChange = { newValue ->
                        paperAngle = newValue
                        handlePaperChange()
                    },
                    label = { Text("版面旋转角") },
                )
            }
        }

    }

}

@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun PrinterPreview() {
    PrinterDropdownMenu(
        PrinterConfigUIState(),
        listOf("ttyS0", "ttyS1", "ttyS2"),
        onPrinterChange = { _, _ -> },
        onPaperChange = { _, _, _, _, _ -> })
}