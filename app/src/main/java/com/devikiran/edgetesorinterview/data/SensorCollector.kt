package com.devikiran.edgetesorinterview.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import com.devikiran.edgetesorinterview.data.model.SensorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


class SensorCollector(
    private val context: Context
) {

    suspend fun collect(): SensorData =
        withContext(Dispatchers.Default) {

            val sm = context.getSystemService<SensorManager>()

            suspend fun read(sensorType: Int): FloatArray? =
                suspendCancellableCoroutine { cont ->
                    val sensor = sm?.getDefaultSensor(sensorType)
                    if (sensor == null) {
                        cont.resume(null)
                        return@suspendCancellableCoroutine
                    }

                    val listener = object : SensorEventListener {
                        override fun onSensorChanged(e: SensorEvent) {
                            sm.unregisterListener(this)
                            cont.resume(e.values.clone())
                        }

                        override fun onAccuracyChanged(s: Sensor?, a: Int) {}
                    }

                    sm.registerListener(
                        listener,
                        sensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )

                    cont.invokeOnCancellation {
                        sm.unregisterListener(listener)
                    }
                }

            val accel = read(Sensor.TYPE_ACCELEROMETER)?.contentToString()  ?: "unavailable"
            val gyro = read(Sensor.TYPE_GYROSCOPE)?.contentToString()  ?: "unavailable"

            SensorData(
                accelerometer = accel,
                gyroscope = gyro
            )
        }
}
