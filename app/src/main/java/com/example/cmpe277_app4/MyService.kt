package com.example.cmpe277_app4

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DetectLabelsRequest
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val imageUri = intent?.extras?.getString("imageUri").toString()
        val imageBytes = intent?.extras?.getByteArray("imageBytes")

        val credential = Credentials(accessKeyId = "XXX", secretAccessKey = "XXX")

        Log.d("MyService", "imageUri = $imageUri")
        Log.d("MyService", "imageByte = $imageBytes")

        Thread {
            CoroutineScope(Dispatchers.IO).launch {
                val souImage = Image {
                    bytes = imageBytes
                }
                val request = DetectLabelsRequest {
                    image = souImage
                    maxLabels = 10
                }
                RekognitionClient {
                    region = "us-west-1"
                    credentialsProvider = StaticCredentialsProvider(credential)
                }.use { rekClient ->
                    val response = rekClient.detectLabels(request)
                    response.labels?.forEach { label ->
                        Log.d("MyService", "detected ${label.name} : ${label.confidence}")
                    }
                    Intent("cmpe277.app4.UPDATE_ACTIVITY").also {
                        it.putExtra("response", response.labels?.map { it -> "${it.name}: ${it.confidence}" }?.toTypedArray())
                        sendBroadcast(it)
                    }
                }
            }
        }.start()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}