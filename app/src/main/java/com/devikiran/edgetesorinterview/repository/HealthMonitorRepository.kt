package com.devikiran.edgetesorinterview.repository

import com.devikiran.edgetesorinterview.data.source.CameraRecorder
import com.devikiran.edgetesorinterview.data.source.GpsCollector
import com.devikiran.edgetesorinterview.data.LogManager
import com.devikiran.edgetesorinterview.data.source.SensorCollector
import com.devikiran.edgetesorinterview.data.source.SystemStatsCollector
import com.devikiran.edgetesorinterview.repository.HealthDataUploader

class HealthMonitorRepository (
    private val systemStatsCollector: SystemStatsCollector,
    private val sensorCollector: SensorCollector,
    private val gpsCollector: GpsCollector,
    private val cameraRecorder: CameraRecorder,
    private val healthDataUploader: HealthDataUploader,
    private val logManager: LogManager
) {

    suspend fun runHealthCycle() {
        try {
            logManager.log("SYSTEM", systemStatsCollector.collect())

            // Sensor data
            val sensorData = sensorCollector.collect()

            logManager.log(
                "ACCEL",
                sensorData.accelerometer
            )

            logManager.log(
                "GYRO",
                sensorData.gyroscope
            )

            logManager.log("GPS", gpsCollector.collect())
            logManager.log("CAMERA", cameraRecorder.recordShortClip())


            healthDataUploader.upload()

            logManager.enforceRetention()

        } catch (e: Exception) {
            logManager.log("ERROR", e.stackTraceToString())
        }
    }

}
