package com.devikiran.edgetesorinterview.data

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import com.devikiran.edgetesorinterview.data.LogManager
import com.devikiran.edgetesorinterview.service.ServiceLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext

class CameraRecorder(
    private val context: Context,
    private val  logManager: LogManager
) {

    suspend fun recordShortClip(): String =
        withContext(Dispatchers.Main) {

            val lifecycleOwner = ServiceLifecycleOwner().apply {
                start()
            }

            val videoFile = logManager.newVideoFile()

            val cameraProvider =
                ProcessCameraProvider.Companion.getInstance(context).await()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.SD))
                .build()

            val videoCapture = VideoCapture.withOutput(recorder)

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                videoCapture
            )

            val recording = videoCapture.output
                .prepareRecording(
                    context,
                    FileOutputOptions.Builder(videoFile).build()
                )
                .start(ContextCompat.getMainExecutor(context)) { }

            delay(5_000)

            recording.stop()

            cameraProvider.unbindAll()
            lifecycleOwner.stop()

            return@withContext videoFile.absolutePath
        }
}