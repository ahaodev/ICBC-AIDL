package com.icbc.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.icbc.selfserviceticketing.deviceservice.printer.QRCodeUtils
import com.icbc.ui.compose.ui.theme.ICBCAIDLTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICBCAIDLTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    APP(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private val configState = MutableStateFlow(ConfigUIState())
private val qrCodeUtils = QRCodeUtils()

@Composable
fun APP(name: String, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val asStateFlow = configState.asStateFlow()
    val uiState = asStateFlow.collectAsState()
    Column {
        Row {
            Text(text = "SN:${uiState.value}", modifier = modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ICBCAIDLTheme {
        APP("Android")
    }
}