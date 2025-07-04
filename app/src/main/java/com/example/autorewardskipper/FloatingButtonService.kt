package com.example.autorewards

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button

class FloatingButtonService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var button: Button

    override fun onCreate() {
        super.onCreate()
        button = Button(this).apply {
            text = "⏹️"
            setOnClickListener {
                stopSelf()
                stopService(Intent(this@FloatingButtonService, ScreenCaptureService::class.java))
                stopService(Intent(this@FloatingButtonService, GestureAutomatorService::class.java))
            }
        }

        val params = WindowManager.LayoutParams(
            200,
            200,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.END or Gravity.TOP
        params.x = 100
        params.y = 300

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(button, params)
    }

    override fun onDestroy() {
        windowManager.removeView(button)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
