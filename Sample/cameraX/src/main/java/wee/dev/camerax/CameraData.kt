package wee.dev.camerax

import android.graphics.Point
import android.graphics.Rect

data class FacePointData(
    var faceRect: Rect,
    var RightEye: Point,
    var LeftEye: Point,
    var Nose: Point,
    var Rightmouth: Point,
    var Leftmouth: Point
)

data class DataGetFacePoint(
    val dataFace: FacePointData?,
    val face: ByteArray?
)