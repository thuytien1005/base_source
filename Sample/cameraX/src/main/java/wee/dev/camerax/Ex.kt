package wee.dev.camerax

import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val RATIO_4_3_VALUE = 4.0 / 3.0

const val RATIO_16_9_VALUE = 16.0 / 9.0

const val MIN_SIZE = 30

val rangeX = 30f..-30f

val rangeY = 30f..-30f

var ratio = 1.0

fun ByteArray.Nv21ToBitmap(width: Int, height: Int): Bitmap {
    val byte = BitmapUtils.NV21toJPEG(this, width, height, 100)
    return BitmapFactory.decodeByteArray(byte, 0, byte.size)
}

fun ProcessCameraProvider?.hasBackCamera(): Boolean {
    return this?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
}

fun ProcessCameraProvider?.hasFrontCamera(): Boolean {
    return this?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
}

fun DisplayMetrics.aSpecRatio(): Int {
    val width = this.widthPixels
    val height = this.heightPixels
    val previewRatio = max(width, height).toDouble() / min(width, height)
    if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
        return AspectRatio.RATIO_4_3
    }
    return AspectRatio.RATIO_16_9
}

fun dp2px(dip: Int, context: Context): Float {
    val scale = context.resources.displayMetrics.density
    return dip * scale + 0.5f
}