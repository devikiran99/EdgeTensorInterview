package com.devikiran.edgetesorinterview.service

import android.app.Service
import android.content.Intent
import com.devikiran.edgetesorinterview.util.NotificationUtil
import com.devikiran.edgetesorinterview.repository.HealthMonitorRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class HealthMonitorService : Service() {

    @Inject
    lateinit var repository: HealthMonitorRepository

    @Inject
    lateinit var notificationUtil: NotificationUtil

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )


    override fun onCreate() {
        super.onCreate()


        notificationUtil.createChannel()

        startForeground(
            1001,
            notificationUtil.foregroundNotification()
        )

        serviceScope.launch {
            while (isActive) {
                repository.runHealthCycle()
                delay(5.minutes)
            }
        }
    }



    override fun onDestroy() {

        serviceScope.cancel()
        startForegroundService(Intent(this, HealthMonitorService::class.java))
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}