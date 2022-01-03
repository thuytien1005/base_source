package com.example.camera.face

import android.graphics.Point
import android.graphics.Rect


data class FaceData(
    val points: Points,
    val faceBytes: ByteArray,
    val frameBytes: ByteArray,
    val isFullFrame: Boolean
) {

    data class Points(
        var faceRect: Rect,
        var rightEye: Point,
        var leftEye: Point,
        var nose: Point,
        var rightMouth: Point,
        var leftMouth: Point
    )
}