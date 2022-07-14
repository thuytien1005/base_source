package wee.digital.camera.face

import android.graphics.Point
import android.graphics.Rect
import android.util.Log

data class FaceDataPoints(
    var faceRect: Rect,
    var RightEye: Point,
    var LeftEye: Point,
    var Nose: Point,
    var Rightmouth: Point,
    var Leftmouth: Point
) {
    fun formatDataFaceHeader(): String? {
        val rect = this.faceRect
        val eyeLeft = this.LeftEye
        val eyeRight = this.RightEye
        val mouthLeft = this.Leftmouth
        val mouthRight = this.Rightmouth
        val nose = this.Nose
        val dataFace =
            "${rect.left}a${rect.top}a${rect.right}a${rect.bottom}a${eyeLeft.x}a${eyeLeft.y}a${eyeRight.x}a${eyeRight.y}a${mouthLeft.x}a${mouthLeft.y}a${mouthRight.x}a${mouthRight.y}a${nose.x}a${nose.y}"
        Log.d("dataFaceUtils", dataFace)
        return dataFace
    }
}