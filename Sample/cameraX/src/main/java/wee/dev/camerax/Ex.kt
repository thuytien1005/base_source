package wee.dev.camerax

import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.graphics.toPoint
import com.google.android.gms.vision.face.Landmark
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

const val RATIO_4_3_VALUE = 4.0 / 3.0

const val RATIO_16_9_VALUE = 16.0 / 9.0

const val MIN_SIZE = 30

val rangeX = 30f..-30f

val rangeY = 30f..-30f

var ratio = 1.0

val highAccuracyOpts = FaceDetectorOptions.Builder()
    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
    .build()

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

fun List<Face>.largestMlKitFace(): Face? {
    var largest: Face? = null
    return if (this.isNotEmpty()) {
        this.forEach {
            if (largest == null) {
                largest = it
            } else if (largest!!.boundingBox.width() < it.boundingBox.width()) {
                largest = it
            }
        }
        largest
    } else {
        largest
    }
}

fun Face.checkFaceOke(): Boolean {
    val x = this.headEulerAngleX
    val y = this.headEulerAngleY
    val boxWidth = this.boundingBox.width()
    return boxWidth >= MIN_SIZE && x in rangeX && y in rangeY
}

/**
 * get dataPoint in face
 */
fun Bitmap.getDataFaceAndFace(face: Face): DataGetFacePoint {
    this

    val rect = face.getRectRatio()

    val extraH = 0.2f
    val extraW = 0.2f

    val plusH = rect.height() * extraH
    val plusW = rect.width() * extraW

    val height = rect.height() + plusH.roundToInt()
    val width = rect.width() + plusW.roundToInt()
    val top = rect.top - (plusH / 2).roundToInt()
    val left = rect.left - (plusW / 2).roundToInt()

    val copiedBitmap = this.copy(Bitmap.Config.ARGB_8888, true)
    try {
        val bmFace = Bitmap.createBitmap(
            copiedBitmap,
            left,
            top,
            width,
            height
        )
        val facePointData =
            face.getDataFace(
                (rect.width() * 0.2f).toInt(),
                (rect.height() * 0.2f).toInt()
            )
        val byteFace = BitmapUtils.bitmapToByteArray(bmFace)
        this.recycle()
        bmFace?.recycle()
        return DataGetFacePoint(facePointData, byteFace)
    } catch (ex: Exception) {
        val facePointData = face.getDataPoint()
        val byteFace = BitmapUtils.bitmapToByteArray(this)
        this.recycle()
        return DataGetFacePoint(facePointData, byteFace)
    }
}

fun Face.getDataPoint(): FacePointData {
    val eyeLeft = this.getLandmark(Landmark.LEFT_EYE)?.position?.getRatio()
    val eyeRight = this.getLandmark(Landmark.RIGHT_EYE)?.position?.getRatio()
    val mouthLeft = this.getLandmark(Landmark.LEFT_MOUTH)?.position?.getRatio()
    val mouthRight = this.getLandmark(Landmark.RIGHT_MOUTH)?.position?.getRatio()
    val nose = this.getLandmark(Landmark.NOSE_BASE)?.position?.getRatio()

    return FacePointData(
        this.getRectRatio(),
        eyeRight!!.toPoint(),
        eyeLeft!!.toPoint(),
        nose!!.toPoint(),
        mouthRight!!.toPoint(),
        mouthLeft!!.toPoint()
    )
}

fun Face.getDataFace(plusW: Int, plusH: Int): FacePointData {
    val faceRect = this.getRectRatio()
    val newRect =
        Rect(
            0 + plusW / 2,
            0 + plusH / 2,
            faceRect.width() + plusW / 2,
            faceRect.height() + plusH / 2
        )

    val eyeLeft = this.getLandmark(Landmark.LEFT_EYE)?.position?.getRatio()
    val eyeRight = this.getLandmark(Landmark.RIGHT_EYE)?.position?.getRatio()
    val mouthLeft = this.getLandmark(Landmark.LEFT_MOUTH)?.position?.getRatio()
    val mouthRight = this.getLandmark(Landmark.RIGHT_MOUTH)?.position?.getRatio()
    val nose = this.getLandmark(Landmark.NOSE_BASE)?.position?.getRatio()

    return FacePointData(
        newRect,
        convertPoint(eyeRight!!.toPoint(), this.boundingBox, plusW, plusH),
        convertPoint(eyeLeft!!.toPoint(), this.boundingBox, plusW, plusH),
        convertPoint(nose!!.toPoint(), this.boundingBox, plusW, plusH),
        convertPoint(mouthRight!!.toPoint(), this.boundingBox, plusW, plusH),
        convertPoint(mouthLeft!!.toPoint(), this.boundingBox, plusW, plusH)
    )
}

private fun convertPoint(oldPoint: Point?, faceRect: Rect, plusW: Int, plusH: Int): Point {
    oldPoint ?: return Point(0, 0)
    val old_X = oldPoint.x
    val old_Y = oldPoint.y
    val new_X = old_X - faceRect.left + plusW / 2
    val new_Y = old_Y - faceRect.top + plusH / 2
    return Point(new_X, new_Y)
}

fun Face.getRectRatio(): Rect {
    val rect = this.boundingBox
    val left = rect.left / ratio
    val top = rect.top / ratio
    val right = rect.right / ratio
    val bottom = rect.bottom / ratio
    return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
}

fun PointF.getRatio(): PointF {
    val x = this.x / ratio.toFloat()
    val y = this.y / ratio.toFloat()
    return PointF(x, y)
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}