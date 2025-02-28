package com.icbc.ui.compose

const val ID_180: String = "ID-180"
const val ID_M40: String = "ID-M40"
const val PRINT_CSN: String = "CSN"
const val PRINT_TSC310E: String = "TSC-310E"
const val PRINT_T321OR331: String = "T3-321/331"
const val PAPER_TYPE_TB: String = "铜版纸"
const val PAPER_TYPE_BLACK: String = "黑标纸"
const val PAPER_TYPE_AGAIN: String = "连续纸"

data class ConfigUIState(
    val scanner: ScannerConfigUIState = ScannerConfigUIState(),
    val printer: PrinterConfigUIState = PrinterConfigUIState(),
    val idCard: IDCardConfigUIState = IDCardConfigUIState(),
    val ttys: List<String> = listOf("ttyS0", "ttyS1", "ttyS2"),
)

data class ScannerConfigUIState(
    val scannerType: String = "HID-Keyboard",
    val serialPort: String = "/dev/ttyACM0",
    val enableScannerSuperLeadSerialPortMode: Boolean = false
)

data class PrinterConfigUIState(

    val printType: String = PRINT_CSN,
    val paperType: String = PAPER_TYPE_TB,
    val paperWidth: String = "80",
    val paperHeight: String = "120",
    val paperPadding: String = "3",
    val rotationAngle: Int = 0,
    val printerTTY: String = "ttyS0",
    val enableBorder: Boolean = false,
)

data class IDCardConfigUIState(
    val idCardType: String = ID_180,
)