package com.icbc.ui.compose

const val ID_180: String = "ID-180"
const val ID_M40: String = "ID-M40"
const val PRINT_CSN: String = "CSN"
const val PRINT_TSC310E: String = "TSC-310E"
const val PAPER_TYPE_TB: String = "铜版纸"
const val PAPER_TYPE_BLACK: String = "黑标纸"
const val PAPER_TYPE_AGAIN: String = "连续纸"

data class ConfigUIState(
    val idCardType: String = ID_180,
    val printType: String = PRINT_CSN,
    val scannerEnableUSBConvertTTY: Boolean = true,
    val paperType: String = PAPER_TYPE_TB,
    val paperWeight: Double = 60.0,
    val paperHeight: Double = 80.0,
    val paperPadding: Double = 5.0,
    val printRote: Int = 0,
)