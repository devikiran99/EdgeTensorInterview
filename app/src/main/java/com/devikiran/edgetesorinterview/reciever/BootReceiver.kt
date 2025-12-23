package com.devikiran.edgetesorinterview.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devikiran.edgetesorinterview.service.HealthMonitorService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startForegroundService(
                Intent(context, HealthMonitorService::class.java)
            )
        }
    }
}