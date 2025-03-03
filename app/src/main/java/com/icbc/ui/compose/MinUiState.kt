package com.icbc.ui.compose

import com.icbc.selfserviceticketing.deviceservice.ID_180
import com.icbc.selfserviceticketing.deviceservice.PAPER_TYPE_CAP
import com.icbc.selfserviceticketing.deviceservice.PRINT_CSN

data class ConfigUIState(
    val scanner: ScannerConfigUIState = ScannerConfigUIState(),
    val printer: PrinterConfigUIState = PrinterConfigUIState(),
    val idCard: IDCardConfigUIState = IDCardConfigUIState(),
    val ttys: List<String> = listOf("ttyS0", "ttyS1", "ttyS2"),
)

data class ScannerConfigUIState(
    val scannerType: String = "HID-Keyboard",
    val serialPort: String = "/dev/ttyS0",
    val enableScannerSuperLeadSerialPortMode: Boolean = false
)

data class PrinterConfigUIState(
    val printType: String = PRINT_CSN,
    val paperType: String = PAPER_TYPE_CAP,
    val paperWidth: String = "0",
    val paperHeight: String = "0",
    val paperPadding: String = "0",
    val rotationAngle: String = "0",
    val printerTTY: String = "ttyS0",
    val enableBorder: Boolean = false,
)

data class IDCardConfigUIState(
    val idCardType: String = ID_180,
)