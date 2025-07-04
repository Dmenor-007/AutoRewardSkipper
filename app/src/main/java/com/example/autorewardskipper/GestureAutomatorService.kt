package com.example.autorewards

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class GestureAutomatorService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun performSwipe() {
        val path = Path().apply {
            moveTo(500f, 1600f)
            lineTo(500f, 400f)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d("GESTURE", "✅ Swipe feito")
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.e("GESTURE", "❌ Swipe cancelado")
            }
        }, null)
    }

    override fun onServiceConnected() {
        RewardReceiver.serviceInstance = this
    }
}
