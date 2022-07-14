package wee.digital.camera

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


fun ProcessCameraProvider?.cameraBack(): Boolean {
    return this?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
}

fun ProcessCameraProvider?.cameraFront(): Boolean {
    return this?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
}

/**
 * handle frame wee.digital.alfar.utils.camera
 */

typealias FrameResult = (nv21: ByteArray, width: Int, height: Int) -> Unit

typealias FrameEvent = (frame: ByteArray, width: Int, height: Int) -> Unit

class LuminosityAnalyzer(listener: FrameEvent? = null) : ImageAnalysis.Analyzer {

    private val listener = ArrayList<FrameEvent>().apply {
        listener?.let { add(it) }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(images: ImageProxy) {
        if (listener.isNullOrEmpty()) {
            images.close()
            return
        }
        val width = images.width
        val height = images.height
        images.image?.let {
            /*val nv21 = it.yuv420toNV21()
            listener.forEach {
                it(nv21, width, height)
            }*/
        }
        images.close()
    }
}



