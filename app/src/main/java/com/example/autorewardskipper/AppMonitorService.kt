package com.example.autorewards

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.util.*

class AppMonitorService : Service() {
    private val handler = Handler()
    private val checkInterval: Long = 2000
    private val allowedApps = listOf("com.zhiliaoapp.musically", "com.kwai.video")

    private val checker = object : Runnable {
        override fun run() {
            val currentApp = getForegroundApp()
            Log.d("APP_MONITOR", "Atual: $currentApp")
            if (currentApp !in allowedApps) {
                stopService(Intent(this@AppMonitorService, ScreenCaptureService::class.java))
            }
            handler.postDelayed(this, checkInterval)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(checker)
        return START_STICKY
    }

    private fun getForegroundApp(): String? {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val begin = end - 10000
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin, end)
        return stats.maxByOrNull { it.lastTimeUsed }?.packageName
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
