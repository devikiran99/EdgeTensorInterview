package com.devikiran.edgetesorinterview.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.SystemClock
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


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        scheduleRestartWatchdog()
        return START_STICKY
    }


    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun scheduleRestartWatchdog() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, HealthMonitorService::class.java)
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10 * 60 * 1000,
            pendingIntent
        )
    }
}