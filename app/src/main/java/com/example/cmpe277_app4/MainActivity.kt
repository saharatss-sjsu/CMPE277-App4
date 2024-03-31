package com.example.cmpe277_app4

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cmpe277_app4.ui.theme.CMPE277App4Theme

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {
    private var responseText by mutableStateOf("")
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val data = intent?.getStringArrayExtra("response")
            Log.d("CallOpenAIService", "response = $data")
            responseText = data?.joinToString(separator="\n") ?: "None"
        }
    }
    private fun processOCR(imageUri: Uri, imageByteArray: ByteArray) {
        val intent = Intent(this, MyService::class.java).apply {
            putExtra("imageUri", imageUri.toString())
            putExtra("imageBytes", imageByteArray)
        }
        startService(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CMPE277App4Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = "AWS OCR Tester", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ImagePicker(context = this@MainActivity) { imageUri, imageByteArray ->
                            processOCR(imageUri, imageByteArray)
                        }
                        OutlinedTextField(
                            value = responseText,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 10,
                            maxLines = 20,
                            readOnly = true,
                            label = {
                                Text(text = "Response")
                            })
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("cmpe277.app4.UPDATE_ACTIVITY")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver, filter, RECEIVER_EXPORTED)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(updateReceiver)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CMPE277App4Theme {
        Greeting("Android")
    }
}