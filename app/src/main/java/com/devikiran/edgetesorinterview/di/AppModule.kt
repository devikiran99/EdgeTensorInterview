package com.devikiran.edgetesorinterview.di

import android.content.Context
import com.devikiran.edgetesorinterview.data.source.CameraRecorder
import com.devikiran.edgetesorinterview.data.source.GpsCollector
import com.devikiran.edgetesorinterview.data.LogManager
import com.devikiran.edgetesorinterview.util.NotificationUtil
import com.devikiran.edgetesorinterview.data.source.SensorCollector
import com.devikiran.edgetesorinterview.data.source.SystemStatsCollector
import com.devikiran.edgetesorinterview.repository.HealthDataUploader
import com.devikiran.edgetesorinterview.network.HealthDataApi
import com.devikiran.edgetesorinterview.repository.HealthMonitorRepository
import com.devikiran.edgetesorinterview.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLogManager(
        @ApplicationContext context: Context
    ): LogManager {
        return LogManager(context)
    }

    @Provides
    @Singleton
    fun provideSystemStatsCollector(
        @ApplicationContext context: Context
    ): SystemStatsCollector {
        return SystemStatsCollector(context)
    }

    @Provides
    @Singleton
    fun provideSensorCollector(
        @ApplicationContext context: Context,
        logManager: LogManager
    ): SensorCollector {
        return SensorCollector(context = context)
    }

    @Provides
    @Singleton
    fun provideGpsCollector(
        @ApplicationContext context: Context
    ): GpsCollector {
        return GpsCollector(context)
    }

    @Provides
    @Singleton
    fun provideCameraRecorder(
        @ApplicationContext context: Context,
        logManager: LogManager
    ): CameraRecorder {
        return CameraRecorder(context = context, logManager = logManager)
    }


    @Provides
    @Singleton
    fun provideTelemetryUploader(
        @ApplicationContext context: Context,
        logManager: LogManager,
        healthDataApi: HealthDataApi
    ): HealthDataUploader {
        return HealthDataUploader(context = context, logManager = logManager, healthDataApi = healthDataApi)
    }


    @Provides
    @Singleton
    fun provideNotificationUtil(@ApplicationContext context: Context): NotificationUtil {
        return NotificationUtil(context)
    }

    @Provides
    @Singleton
    fun provideHealthMonitorRepository(
        systemStatsCollector: SystemStatsCollector,
        sensorCollector: SensorCollector,
        gpsCollector: GpsCollector,
        cameraRecorder: CameraRecorder,
        healthDataUploader: HealthDataUploader,
        logManager: LogManager
    ): HealthMonitorRepository {
        return HealthMonitorRepository(
            systemStatsCollector = systemStatsCollector,
            sensorCollector = sensorCollector,
            gpsCollector = gpsCollector,
            cameraRecorder = cameraRecorder,
            healthDataUploader = healthDataUploader,
            logManager = logManager
        )
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTelemetryApi(
        retrofit: Retrofit
    ): HealthDataApi {
        return retrofit.create(HealthDataApi::class.java)
    }
}
