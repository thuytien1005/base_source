package wee.digital.camera.face

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import androidx.core.graphics.toPoint
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import wee.digital.camera.log
import kotlin.math.roundToInt

val FaceDetector.Companion.MIN_BLUR get() = 10000000.0
val FaceDetector.Companion.RESIZE_BLUR get() = 0.25
val FaceDetector.Companion.RESIZE get() = 480.0
val FaceDetector.Companion.MIN_SIZE get() = 22
val FaceDetector.Companion.rangeX get() = -15f..15f
val FaceDetector.Companion.rangeY get() =  -15f..15f
val FaceDetector.Companion.rangeZ get() = -10f..10f

class ProcessException(message: String? = null) : IllegalArgumentException(message)

class NoFaceException(message: String? = null) : NullPointerException(message)

class InvalidFaceException(message: String? = null) : IllegalArgumentException(message)

val mlKitFaceDetector by lazy {
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        //.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.4f)
        .enableTracking()
        .build()
    FaceDetection.getClient(options)
}

fun Rect.checkZoneHDFaceMlKit(): Boolean {
    return try {
        val x = this.exactCenterX()
        val y = this.exactCenterY()
        log.d("checkZoneFaceMlKit : x $x - y $y")
        x in 100f..380f
    } catch (e: java.lang.Exception) {
        false
    }
}

var ratio = 1.0

fun Face.getRectRatio(sRatio: Double = ratio): Rect {
    val rect = this.boundingBox
    val left = rect.left / sRatio
    val top = rect.top / sRatio
    val right = rect.right / sRatio
    val bottom = rect.bottom / sRatio
    return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
}

fun Face.getDataPoint(): FaceDataPoints {
    val eyeLeft = this.getLandmark(FaceLandmark.LEFT_EYE)?.position?.getRatio()
    val eyeRight = this.getLandmark(FaceLandmark.RIGHT_EYE)?.position?.getRatio()
    val mouthLeft = this.getLandmark(FaceLandmark.MOUTH_LEFT)?.position?.getRatio()
    val mouthRight = this.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position?.getRatio()
    val nose = this.getLandmark(FaceLandmark.NOSE_BASE)?.position?.getRatio()
    return FaceDataPoints(
        this.getRectRatio(),
        eyeRight!!.toPoint(),
        eyeLeft!!.toPoint(),
        nose!!.toPoint(),
        mouthRight!!.toPoint(),
        mouthLeft!!.toPoint()
    )
}

fun Face.getDataFace(plusW: Int, plusH: Int): FaceDataPoints {
    val faceRect = this.getRectRatio()
    val newRect =
        Rect(
            0 + plusW / 2,
            0 + plusH / 2,
            faceRect.width() + plusW / 2,
            faceRect.height() + plusH / 2
        )

    val eyeLeft = this.getLandmark(FaceLandmark.LEFT_EYE)?.position?.getRatio()
    val eyeRight = this.getLandmark(FaceLandmark.RIGHT_EYE)?.position?.getRatio()
    val mouthLeft = this.getLandmark(FaceLandmark.MOUTH_LEFT)?.position?.getRatio()
    val mouthRight = this.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position?.getRatio()
    val nose = this.getLandmark(FaceLandmark.NOSE_BASE)?.position?.getRatio()

    return FaceDataPoints(
        newRect,
        eyeRight?.toPoint().convertPoint(this.boundingBox, plusW, plusH),
        eyeLeft?.toPoint().convertPoint(this.boundingBox, plusW, plusH),
        nose?.toPoint().convertPoint(this.boundingBox, plusW, plusH),
        mouthRight?.toPoint().convertPoint(this.boundingBox, plusW, plusH),
        mouthLeft?.toPoint().convertPoint(this.boundingBox, plusW, plusH)
    )
}

fun Face.checkHeadEuler(): Boolean {
    val x = this.headEulerAngleX
    val y = this.headEulerAngleY
    return x in FaceDetector.rangeX && y in FaceDetector.rangeY
}

fun PointF.getRatio(): PointF {
    val x = this.x / ratio.toFloat()
    val y = this.y / ratio.toFloat()
    return PointF(x, y)
}

fun Point?.convertPoint(faceRect: Rect, plusW: Int, plusH: Int): Point {
    this ?: return Point(0, 0)
    val oldX = this.x
    val oldY = this.y
    val newX = oldX - faceRect.left + plusW / 2
    val newY = oldY - faceRect.top + plusH / 2
    return Point(newX, newY)
}