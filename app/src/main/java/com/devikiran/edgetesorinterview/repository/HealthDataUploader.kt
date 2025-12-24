package com.devikiran.edgetesorinterview.repository

import android.content.Context
import android.os.Build
import com.devikiran.edgetesorinterview.data.LogManager
import com.devikiran.edgetesorinterview.network.HealthDataApi
import com.devikiran.edgetesorinterview.network.model.HealthData
import com.devikiran.edgetesorinterview.util.NetworkUtils

class HealthDataUploader(
    private val context: Context,
    private val logManager: LogManager,
    private val healthDataApi: HealthDataApi
) {

    suspend fun upload() {

        if (!NetworkUtils.isInternetAvailable(context)) return

        val logs = logManager.latestSnapshot()

        if (logs.isBlank()) return

        val payload = HealthData(
            deviceId = Build.MODEL ?: "unknown",
            timestamp = System.currentTimeMillis(),
            logs = logs
        )

        try {
            val response = healthDataApi.uploadTelemetry(payload)
            if (!response.isSuccessful) {
                logManager.log("Error", response.errorBody().toString())
            }
        } catch (e: Exception) {
            logManager.log("Error", e.message.toString())
        }
    }
}