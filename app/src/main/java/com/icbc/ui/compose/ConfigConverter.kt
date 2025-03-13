package com.icbc.ui.compose

import com.blankj.utilcode.util.ToastUtils
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.SCAN_SUPERLEAD

object ConfigConverter {
    fun toUIState(config: Config): ConfigUIState {
        val idCardType = config.idCardType
        return ConfigUIState(
            idCard = IDCardConfigUIState(idCardType = idCardType),
            printer = PrinterConfigUIState(
                printType = config.printerType,
                paperType = config.paperType,
                paperWidth = config.width.toString(),
                paperHeight = config.height.toString(),
                paperPadding = config.margin.toString(),
                rotationAngle = config.rotation.toString(),
                printerTTY = config.csnDevPort,
                enableBorder = config.enableBorder,
                enableCutter = config.enableCutter,
            ),
            scanner = ScannerConfigUIState(
                scannerType = when (config.scannerType) {
                    SCAN_SUPERLEAD -> "HID-Keyboard"
                    else -> "HID-Keyboard"
                },
                serialPort = config.csnDevPort,
                enableScannerSuperLeadSerialPortMode = config.enableScannerSuperLeadSerialPortMode
            ),
        )
    }

    fun toConfig(uiState: ConfigUIState): Config {
        var rotation = 0
        var height = 0f
        var width = 0f
        var margin = 0f
        try {
            rotation = uiState.printer.rotationAngle.toInt()
            margin = uiState.printer.paperPadding.toFloat()
            width = uiState.printer.paperWidth.toFloat()
            height = uiState.printer.paperHeight.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.showLong("数值输入错误")
        }

        return Config(
            idCardType = uiState.idCard.idCardType,
            rotation = rotation,
            scannerType = when (uiState.scanner.scannerType) {
                "HID-Keyboard" -> SCAN_SUPERLEAD
                else -> SCAN_SUPERLEAD
            },
            printerType = uiState.printer.printType,
            paperType = uiState.printer.paperType,
            margin = margin,
            width = width,
            height = height,
            enableCutter = uiState.printer.enableCutter,
            enableBorder = uiState.printer.enableBorder,
            csnDevPort = uiState.scanner.serialPort,
            enableScannerSuperLeadSerialPortMode = uiState.scanner.enableScannerSuperLeadSerialPortMode
        )
    }
}