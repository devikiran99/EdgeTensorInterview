package com.devikiran.edgetesorinterview.network

import com.devikiran.edgetesorinterview.network.model.HealthData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HealthDataApi {

    @POST("api/dashcam/data")
    suspend fun uploadTelemetry(
        @Body payload: HealthData
    ): Response<Unit>
}