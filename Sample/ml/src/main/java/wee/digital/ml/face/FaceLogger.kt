package wee.digital.ml.face

import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import java.util.*

object FaceLogger {


    // Frame count that have been processed so far in an one second interval to calculate FPS.
    private var frameProcessedInOneSecondInterval = 0

    // Used to calculate latency, running in the same thread, no sync needed.
    private var numRuns = 0

    private var framesPerSecond = 0

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

    private val fpsTimer = Timer().also {
        it.scheduleAtFixedRate(object : java.util.TimerTask() {
            override fun run() {
                framesPerSecond = frameProcessedInOneSecondInterval
                frameProcessedInOneSecondInterval = 0
            }
        }, 0, 1000)
    }

    fun clear() {
        fpsTimer.cancel()
        resetLatencyStats()
    }

    private fun resetLatencyStats() {
        numRuns = 0
    }

    fun processInfo(frameStartMs: Long, detectorStartMs: Long): FaceDetector.Info? {
        val endProcessTime = System.currentTimeMillis()
        val currentFrameLatencyMs = endProcessTime - frameStartMs
        val currentDetectorLatencyMs = endProcessTime - detectorStartMs
        if (numRuns >= 500) {
            resetLatencyStats()
        }
        numRuns++
        frameProcessedInOneSecondInterval++
        // Only log inference info once per second. When frameProcessedInOneSecondInterval is
        // equal to 1, it means this is the first frame processed during the current second.
        if (frameProcessedInOneSecondInterval == 1) {
            return FaceDetector.Info(numRuns, currentFrameLatencyMs, currentDetectorLatencyMs, framesPerSecond)
        }
        return null
    }

    fun faceText(face: Face): String {
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
        return sb.toString()
    }


}