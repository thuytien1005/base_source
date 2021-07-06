package wee.digital.ml.face

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import wee.digital.ml.base.BitmapUtils
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.base.ScopedExecutor

class FaceDetector {

    data class Info(
            val numRuns: Int,
            val frameLatency: Long,
            val detectorLatency: Long,
            val framesPerSecond: Int,
    )

    interface Interface {

        /* """
                 Num of Runs: $numRuns
                 Frame latency: $currentFrameLatencyMs
                 Detector latency: $currentDetectorLatencyMs
                 Fps: $framesPerSecond
                 Memory available in system: $availableMemoryInMB MB
             """.trimIndent()*/
        fun onProcessInfo(info: Info)

        fun onProcessSuccess(face: Face)

        fun onProcessFailure(e: Exception)
    }

    private var int: Interface? = null

    private val now get() = SystemClock.elapsedRealtime()

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    // Whether this processor is already shut down
    private var isShutdown = false

    private val detector: FaceDetector = FaceDetection.getClient(faceOptions.detectorOptions)

    fun process(image: ImageProxy, graphicOverlay: GraphicOverlay?) {
        if (isShutdown) return
        try {
            val frameStartTime = now

            @SuppressLint("UnsafeExperimentalUsageError")
            val bitmap = BitmapUtils.getBitmap(image)

            @SuppressLint("UnsafeExperimentalUsageError")
            val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

            val detectorStartTime = now

            detector.process(inputImage)
                    .addOnSuccessListener(executor) { results: List<Face> ->
                        FaceLogger.processInfo(frameStartTime, detectorStartTime)
                        graphicOverlay?.clear()
                        if (bitmap != null) {
                            //graphicOverlay.add(CameraImageGraphic(graphicOverlay, bitmap))
                        }
                        results.firstOrNull()?.also {
                            //graphicOverlay.add(FaceGraphic(graphicOverlay, it))
                            int?.onProcessSuccess(it)
                        }
                        //graphicOverlay.postInvalidate()
                    }
                    .addOnFailureListener(executor) { e: Exception ->
                        //graphicOverlay.clear()
                        //graphicOverlay.postInvalidate()
                        int?.onProcessFailure(e)
                    }
                    .addOnCompleteListener {
                        image.close()
                    }


        } catch (e: Exception) {

        }
    }

    fun stopProcess() {
        isShutdown = true
        executor.shutdown()
        FaceLogger.clear()
        detector.close()
    }


}