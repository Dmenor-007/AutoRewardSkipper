package com.example.autorewards

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class RewardReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("RECEIVER", "🏆 Recompensa recebida → Swipe")
        serviceInstance?.performSwipe()
    }

    companion object {
        var serviceInstance: GestureAutomatorService? = null
    }
}
