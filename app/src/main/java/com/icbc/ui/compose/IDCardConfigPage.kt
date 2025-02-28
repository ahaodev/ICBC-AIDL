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

@Composable
fun IDCardDropdownMenu(
    idCard: IDCardConfigUIState,
    ttys: List<String>,
    onIDCardConfigChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // This is fine
    var selectedOption by remember { mutableStateOf(ID_M40) } // This is fine
    val options = listOf(ID_M40, ID_180)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "选择读卡器:")
                Box {
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
                                    selectedOption = option
                                    expanded = false
                                    onIDCardConfigChange(option)
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
    IDCardDropdownMenu(IDCardConfigUIState(), listOf("ttyS0", "ttyS1", "ttyS2")){

    }
}