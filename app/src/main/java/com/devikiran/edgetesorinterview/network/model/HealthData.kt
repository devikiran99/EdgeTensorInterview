package com.devikiran.edgetesorinterview.network.model

data class HealthData(
    val deviceId: String,
    val timestamp: Long,
    val logs: String
)