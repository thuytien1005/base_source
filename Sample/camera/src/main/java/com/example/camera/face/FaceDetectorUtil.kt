package com.example.camera.face

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import com.example.camera.bitmapToByteArray
import com.example.camera.util.Logger
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.roundToInt

private val log = Logger("DetectExtension")


fun checkZoneFace(face: Face?, heightCamera: Int, widthCamera: Int): Boolean {
    face ?: return false
    val centerX = face.boundingBox.centerX()
    val centerY = face.boundingBox.centerY()
    val ya = (heightCamera / 2.5).toInt()
    val yb = heightCamera - (heightCamera / 3)
    val xa = widthCamera / 3
    val xb = widthCamera - (widthCamera / 3)
    val inZone = centerX in xa..xb && centerY in ya..yb
    Log.e(
        "CheckZoneFace",
        "X: $centerX - Y: $centerY - Zone: $xa .. $xb $ya .. $yb - InZone: $inZone"
    )
    return inZone
}

fun Face?.checkEye(): Boolean {
    this ?: return false
    val eyeLeft =
        this.getLandmark(FaceLandmark.LEFT_EYE)?.position ?: return false
    val eyeRight = this.getLandmark(FaceLandmark.RIGHT_EYE)?.position ?: return false
    log.d("eyeLeft ${eyeLeft.x} ${eyeLeft.y}")
    log.d("eyeRight ${eyeRight.x} ${eyeRight.y}")
    val checkY = eyeLeft.y in 400f..498f && eyeRight.y in 400f..498f
    val checkX = eyeLeft.x <= 376f && eyeRight.x >= 380f
    return checkY && checkX
}

/**
 * get data point to face
 */
fun getFaceData(image: Bitmap, face: Face): FaceData {
    val rect = face.boundingBox
    val extraH = 0.5f
    val extraW = 0.5f
    val plusH = rect.height() * extraH
    val plusW = rect.width() * extraW
    val height = rect.height() + plusH.roundToInt()
    val width = rect.width() + plusW.roundToInt()
    val top = rect.top - (plusH / 2).roundToInt()
    val left = rect.left - (plusW / 2).roundToInt()

    val frameBytes: ByteArray = image.bitmapToByteArray()
        ?: throw NullPointerException("Get frame byte array from Bitmap is null")
    return try {
        val copiedBitmap = image.copy(Bitmap.Config.RGB_565, true)
        val faceBitmap = Bitmap.createBitmap(copiedBitmap, left, top, width, height)
        val faceBytes = faceBitmap.bitmapToByteArray()
            ?: throw NullPointerException("Get face byte array from Bitmap is null")
        val dataPoints = getDataFaceCrop(face, (rect.width() * 0.5f).toInt())
        image.recycle()
        faceBitmap?.recycle()
        FaceData(dataPoints, faceBytes, frameBytes, false)
    } catch (ex: Exception) {

        val dataPoints = getDataFullFace(face)
        image.recycle()
        FaceData(dataPoints, frameBytes, frameBytes, true)
    }
}

private fun convertPointFaceCrop(
    oldPoint: PointF?,
    faceRect: Rect,
    plus: Int
): Point {
    oldPoint ?: return Point(0, 0)
    val old_X = oldPoint.x.roundToInt()
    val old_Y = oldPoint.y.roundToInt()
    val new_X = old_X - faceRect.left + plus / 2
    val new_Y = old_Y - faceRect.top + plus / 2
    return Point(new_X, new_Y)
}

private fun getDataFaceCrop(face: Face, plus: Int): FaceData.Points {
    val leftTop = convertPointFaceCrop(
        PointF(face.boundingBox.left.toFloat(), face.boundingBox.top.toFloat()),
        face.boundingBox,
        plus
    )
    val rightBottom = convertPointFaceCrop(
        PointF(face.boundingBox.right.toFloat(), face.boundingBox.bottom.toFloat()),
        face.boundingBox,
        plus
    )

    val newRect = Rect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y)

    val leftEye = convertPointFaceCrop(
        face.getLandmark(FaceLandmark.LEFT_EYE)?.position,
        face.boundingBox,
        plus
    )
    val rightEye = convertPointFaceCrop(
        face.getLandmark(FaceLandmark.RIGHT_EYE)?.position,
        face.boundingBox,
        plus
    )
    val leftMouth = convertPointFaceCrop(
        face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position,
        face.boundingBox,
        plus
    )
    val rightMouth = convertPointFaceCrop(
        face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position,
        face.boundingBox,
        plus
    )
    val nose = convertPointFaceCrop(
        face.getLandmark(FaceLandmark.NOSE_BASE)?.position,
        face.boundingBox,
        plus
    )
    return FaceData.Points(newRect, rightEye, leftEye, nose, rightMouth, leftMouth)
}

private fun getDataFullFace(face: Face): FaceData.Points {
    val leftTop = convertPointFullFrame(
        PointF(face.boundingBox.left.toFloat(), face.boundingBox.top.toFloat())
    )
    val rightBottom = convertPointFullFrame(
        PointF(face.boundingBox.right.toFloat(), face.boundingBox.bottom.toFloat())
    )

    val newRect = Rect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y)

    val leftEye = convertPointFullFrame(
        face.getLandmark(FaceLandmark.LEFT_EYE)?.position
    )
    val rightEye = convertPointFullFrame(
        face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
    )
    val leftMouth = convertPointFullFrame(
        face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position
    )
    val rightMouth = convertPointFullFrame(
        face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position
    )
    val nose = convertPointFullFrame(
        face.getLandmark(FaceLandmark.NOSE_BASE)?.position
    )
    return FaceData.Points(newRect, rightEye, leftEye, nose, rightMouth, leftMouth)
}

private fun convertPointFullFrame(oldPoint: PointF?): Point {
    oldPoint ?: return Point(0, 0)
    val old_X = oldPoint.x.roundToInt()
    val old_Y = oldPoint.y.roundToInt()
    return Point(old_X, old_Y)
}


fun cropBitmapWithRect(bitmap: Bitmap, rect: Rect): Bitmap? {
    return try {
        val cropBitmap = Bitmap.createBitmap(
            bitmap,
            rect.left, rect.top, rect.width(), rect.height()
        )
        cropBitmap
    } catch (ex: Exception) {
        Log.e("getFaceResult", ex.message.toString())
        null
    }
}
