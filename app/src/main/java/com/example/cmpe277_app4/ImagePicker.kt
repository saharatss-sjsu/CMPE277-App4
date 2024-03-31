package com.example.cmpe277_app4

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import java.io.ByteArrayOutputStream
import java.io.InputStream


@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImagePicker(context: Context, processOCR: (Uri, ByteArray) -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data

            val inputStream: InputStream? = imageUri?.let {
                context.contentResolver.openInputStream(
                    it
                )
            }
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)

            var len = 0
            while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            val imageByte = byteBuffer.toByteArray()

            processOCR(imageUri!!, imageByte)
        }
    }

    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                launcher.launch(intent)
            }) {
                Text("Pick an Image")
            }
            imageUri?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }


}