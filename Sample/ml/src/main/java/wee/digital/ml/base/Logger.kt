package wee.digital.ml.base

import android.util.Log
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import wee.digital.ml.BuildConfig


open class Logger(private val tag: String) {

    fun d(s: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, s.toString())
        }
    }

    fun e(e: Throwable) {
        Log.e(tag, e.message.toString())
    }
}

class FaceDetectorLogger : Logger("FaceDetector") {

    private val landMarkTypes = intArrayOf(
            FaceLandmark.MOUTH_BOTTOM, FaceLandmark.MOUTH_RIGHT, FaceLandmark.MOUTH_LEFT,
            FaceLandmark.RIGHT_EYE, FaceLandmark.LEFT_EYE,
            FaceLandmark.RIGHT_EAR, FaceLandmark.LEFT_EAR,
            FaceLandmark.RIGHT_CHEEK, FaceLandmark.LEFT_CHEEK,
            FaceLandmark.NOSE_BASE
    )

    private val landMarkText = arrayOf(
            "mouth bottom", "mouth right", "mouth left",
            "right eye", "left eye",
            "right ear", "left ear",
            "right cheek", "left cheek",
            "nose base"
    )

    fun d(face: Face) {
        val sb = StringBuilder()
        sb.append("""
        Face:
        Bounding box: ${face.boundingBox.flattenToString()}
        Euler Angle X: ${face.headEulerAngleX}
                    Y: ${face.headEulerAngleY}
                    Z: ${face.headEulerAngleZ}
    """.trimIndent())
        for (i in landMarkTypes.indices) {
            val position = face.getLandmark(landMarkTypes[i])?.position ?: continue
            sb.append("\n%s: x: %f , y: %f".format(landMarkText[i], position.x, position.y))
        }
        sb.append("\nEyes open probability: left:${face.leftEyeOpenProbability}, right:${face.rightEyeOpenProbability}")
        sb.append("\nSmiling probability: ${face.smilingProbability}")
        d(sb.toString())
    }
}