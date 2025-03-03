package com.icbc.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.icbc.selfserviceticketing.deviceservice.ID_180
import com.icbc.selfserviceticketing.deviceservice.ID_M40

@Composable
fun IDCardConfigPage(
    idCard: IDCardConfigUIState,
    ttys: List<String>, // 未使用，但保留参数以保持签名一致
    onConfigChange: (newConfig: IDCardConfigUIState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(ID_M40, ID_180)

    fun handleConfigChange(idCardType: String = idCard.idCardType) {
        onConfigChange(IDCardConfigUIState(idCardType = idCardType))
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "选择读卡器:")
                Box(modifier = Modifier.padding(start = 8.dp)) {
                    TextButton(onClick = { expanded = true }) {
                        Text(idCard.idCardType)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    handleConfigChange(idCardType = option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun IDCardPreview() {
    IDCardConfigPage(IDCardConfigUIState(), listOf("ttyS0", "ttyS1", "ttyS2")){

    }
}