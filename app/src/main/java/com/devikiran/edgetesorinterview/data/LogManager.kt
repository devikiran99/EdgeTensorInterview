package com.devikiran.edgetesorinterview.data

import android.content.Context
import android.util.Log
import java.io.File
import java.time.Instant

class LogManager(private val context: Context) {

    private var logDir: File = File(context.filesDir, "logs").apply {
        if (!exists()) mkdirs()
    }
    private var logFile: File = File(logDir, "dashcam.txt")

    private var videoDir: File = File(context.filesDir, "videos").apply {
        if (!exists()) mkdirs()
    }

    @Synchronized
    fun log(tag: String, message: String?) {

        val line =
            "${Instant.now()} [$tag] ${message ?: "null"}\n"

        try {
            logFile.appendText(line)
            Log.d("LogManager", line)
        } catch (e: Exception) {
            Log.e("LogManager", "Failed to write log", e)
        }
    }

    fun enforceRetention() {
        rotateLogIfNeeded()
        pruneOldVideos()
    }

    private fun rotateLogIfNeeded() {
        if (!logFile.exists()) return

        if (logFile.length() < MAX_LOG_SIZE_BYTES) return

        val rotated = File(
            logDir,
            "dashcam_${System.currentTimeMillis()}.txt"
        )

        val renamed = logFile.renameTo(rotated)
        if (!renamed) {
            logFile.delete()
        }
    }

    fun newVideoFile(): File {
        return File(
            videoDir,
            "vid_${System.currentTimeMillis()}.mp4"
        )
    }

    private fun pruneOldVideos() {
        val videos = videoDir
            .listFiles { f -> f.extension == "mp4" }
            ?.sortedBy { it.lastModified() }
            ?: return

        if (videos.size <= MAX_VIDEO_FILES) return

        videos
            .take(videos.size - MAX_VIDEO_FILES)
            .forEach {
                try {
                    it.delete()
                } catch (_: Exception) {
                }
            }
    }


    fun latestSnapshot(maxLines: Int = 50): String {
        if (!logFile.exists()) return ""

        return try {
            logFile
                .readLines()
                .takeLast(maxLines)
                .joinToString("\n")
        } catch (e: Exception) {
            ""
        }
    }

    companion object{
        private const val MAX_LOG_SIZE_BYTES = 5 * 1024 * 1024 // 5 MB
        private const val MAX_VIDEO_FILES = 10
    }
}