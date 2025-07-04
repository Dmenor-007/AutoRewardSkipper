package com.example.autorewards

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        toggleButton = findViewById(R.id.toggleButton)

        checkPermissions()

        toggleButton.setOnClickListener {
            if (!isRunning) {
                startAutomation()
            } else {
                stopAutomation()
            }
        }

        val configBtn = Button(this).apply {
            text = "Configurações"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, ConfigActivity::class.java))
            }
        }
        (findViewById<LinearLayout>(R.id.rootLayout)).addView(configBtn)
    }

    private fun startAutomation() {
        startService(Intent(this, AppMonitorService::class.java))
        startService(Intent(this, FloatingButtonService::class.java))
        isRunning = true
        statusText.text = "🔄 Monitorando Kwai/TikTok..."
        toggleButton.text = "Parar Automação"
    }

    private fun stopAutomation() {
        stopService(Intent(this, AppMonitorService::class.java))
        stopService(Intent(this, ScreenCaptureService::class.java))
        stopService(Intent(this, FloatingButtonService::class.java))
        isRunning = false
        statusText.text = "⏹️ Parado"
        toggleButton.text = "Iniciar Automação"
    }

    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Ative permissão de sobreposição", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }

        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }
}