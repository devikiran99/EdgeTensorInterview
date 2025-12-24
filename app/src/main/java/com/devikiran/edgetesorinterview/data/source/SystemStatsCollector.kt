package com.devikiran.edgetesorinterview.data.source

import android.app.ActivityManager
import android.content.Context
import android.os.StatFs
import androidx.core.content.getSystemService

class SystemStatsCollector(private val context: Context) {
    fun collect(): String {
        val am = context.getSystemService<ActivityManager>()
        val mem = ActivityManager.MemoryInfo().apply { am?.getMemoryInfo(this) }

        val runtime = Runtime.getRuntime()

        return buildString {
            append("RAM: total=${mem.totalMem}, free=${mem.availMem}\n")
            append("AppMem: used=${runtime.totalMemory() - runtime.freeMemory()}\n")
            append("Storage: ${StatFs(context.filesDir.path).availableBytes}\n")
            append("CPU: ${readCpuUsage()}%")
        }
    }

    private fun readCpuUsage(): Float {
        //TODO yet to implement
        return 0.0f
    }
}