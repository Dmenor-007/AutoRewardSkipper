package com.example.autorewards

import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.*
import java.nio.ByteBuffer

class ScreenCaptureService : Service() {
    private lateinit var mediaProjection: MediaProjection
    private lateinit var imageReader: ImageReader
    private lateinit var virtualDisplay: VirtualDisplay
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("resultCode", -1) ?: return START_NOT_STICKY
        val data = intent.getParcelableExtra<Intent>("data") ?: return START_NOT_STICKY
        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, data)!!

        val metrics = Resources.getSystem().displayMetrics
        imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            imageReader.surface,
            null, null
        )

        startForegroundNotification()
        captureLoop()

        return START_STICKY
    }

    private fun startForegroundNotification() {
        val channelId = "screen_capture"
        val channel = NotificationChannel(channelId, "Screen Capture", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("OCR ativo")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()

        startForeground(1, notification)
    }

    private fun captureLoop() {
        coroutineScope.launch {
            while (true) {
                val image = imageReader.acquireLatestImage()
                if (image != null) {
                    val bitmap = imageToBitmap(image)
                    image.close()
                    processBitmap(bitmap)
                }
                delay(2000)
            }
        }
    }

    private fun imageToBitmap(image: android.media.Image): Bitmap {
        val plane = image.planes[0]
        val buffer: ByteBuffer = plane.buffer
        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bmp = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bmp.copyPixelsFromBuffer(buffer)
        return Bitmap.createBitmap(bmp, 0, 0, image.width, image.height)
    }

    private fun processBitmap(bitmap: Bitmap) {
        val recognizer = TextRecognition.getClient()
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val prefs = getSharedPreferences("config", Context.MODE_PRIVATE)
                val keywords = prefs.getStringSet("keywords", setOf("ganhou", "moedas", "recompensa")) ?: setOf()
                val text = visionText.text.lowercase()
                if (keywords.any { text.contains(it.lowercase()) }) {
                    Log.d("OCR", "🔔 Recompensa detectada: $text")
                    sendBroadcast(Intent("com.auto.REWARD_DETECTED"))
                }
            }
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        mediaProjection.stop()
        imageReader.close()
        virtualDisplay.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
