package wee.dev.camerax

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

typealias LumaListener = (frame: ByteArray, width: Int, height: Int) -> Unit

class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {

    private var isProcessing = false

    private val frameRateWindow = 8

    private val frameTimestamps = ArrayDeque<Long>(5)

    private val listener = ArrayList<LumaListener>().apply {
        listener?.let { add(it) }
    }

    private var lastAnalyzedTimestap = 0L

    var framesPerSecond: Double = -1.0
        private set

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(images: ImageProxy) {
        if (isProcessing) return
        isProcessing = true
        if (listener.isNullOrEmpty()) {
            images.close()
            return
        }
        val width = images.width
        val height = images.height
        val nv21 = BitmapUtils.YUV420toNV21(images.image)
        listener.forEach { it(nv21, width, height) }
        images.close()
        isProcessing = false
    }
}