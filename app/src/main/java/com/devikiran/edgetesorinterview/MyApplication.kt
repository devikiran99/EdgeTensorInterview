package com.devikiran.edgetesorinterview

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class MyApplication : Application() {
    override fun onCreate() {
        application = this
        super.onCreate()
    }

    companion object {
        @JvmStatic
        var application: MyApplication?= null
            private set
    }
}