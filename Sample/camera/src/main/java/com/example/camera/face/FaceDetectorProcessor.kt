package com.example.camera.face

import android.annotation.SuppressLint
import android.media.Image
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.camera.core.ImageProxy
import androidx.core.graphics.toRectF
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.camera.util.Logger
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.*

class FaceDetectorProcessor(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val detectorCallback: FaceDetectorCallback
) {

    companion object {
        private val faceDetector: FaceDetector by lazy {
            FaceDetection.getClient(FaceDetectorOpt.faceOption)
        }
    }

    private val log = Logger("FaceDetector")

    private var faceDetectJob: Job? = null

    private var callbackDelayJob: Job? = null

    private var isHadFace: Boolean = false

    private var faceHadEligible: Boolean = false

    private var isPause: Boolean = true

    private val isProcessing: Boolean get() = faceDetectJob?.isActive ?: false

    private var currentFaceData: FaceData? = null

    fun start() {
        detectorCallback.detectedNoFace()
        GlobalScope.launch {
            delay(200)
            isHadFace = false
            faceHadEligible = false
            isPause = false
        }
    }

    fun pause() {
        isPause = true
        faceDetectJob?.cancel()
    }

    fun onProcess(imageProxy: ImageProxy) {
        if (isPause || isProcessing) {
            imageProxy.close()
            return
        }
        log.d("start process an ImageProxy")
        faceDetectJob = lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                process(imageProxy)
            }.getOrElse {
                log.e(it)
            }
            imageProxy.close()
        }
    }

    @WorkerThread
    @SuppressLint("UnsafeOptInUsageError")
    private fun process(imageProxy: ImageProxy) {

        val img = imageProxy.image as Image
        val visionImage = InputImage.fromMediaImage(img, imageProxy.imageInfo.rotationDegrees)

        val detectResult: List<Face> = Tasks.await(faceDetector.process(visionImage))
        val faceResult = detectResult.maxByOrNull { it.boundingBox.width() }

        log.d("check is has face")
        val hasFace: Boolean = faceResult != null
        if (isHadFace != hasFace) {
            isHadFace = hasFace
            callbackDelayJob?.cancel()
            callbackDelayJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(1200)
                if (isHadFace) {
                    detectorCallback.detectedHasFace()
                } else {
                    detectorCallback.detectedNoFace()
                }
            }
        }

        if (hasFace) {

            val face = faceResult!!
            val boundingBox = face.boundingBox

            log.d(
                "BoundingBox: Left ${boundingBox.left} - Right: ${boundingBox.right} " +
                        "Top ${boundingBox.top} - Bottom: ${boundingBox.bottom}"
            )
            boundingBox.toRectF()
            visionImage.width
            visionImage.height
            // Image proxy Width:1280 - Height: 960
            // Min: Left 239 - Right: 696 Top 424 - Bottom: 879 - Width: 456
            // Max: Left 96 - Right: 845 Top 259 - Bottom: 1008 - Width: 750

            val widthIsOk = boundingBox.width() in 400..800
            val headEulerIsOk =
                face.headEulerAngleY in -20f..20f && face.headEulerAngleZ in -20f..20f
            val centerIsOk = boundingBox.left < imageProxy.width * 0.3F &&
                    boundingBox.right > imageProxy.width * 0.7F
            /*  boundingBox.top > imageProxy.width * 0.7F &&*/

            log.d("IsOk: width: $widthIsOk - headEuler: $headEulerIsOk - center: $centerIsOk")

        }


        /*if (faceIsOk) {
            val bitmap = img.yuv420toNV21()
                    .nv21ToBitmap(imageProxy.width, imageProxy.height)
                    .rotate(visionImage.rotationDegrees) ?: return
            val faceData: FaceData? = getFaceData(bitmap, faceResult!!)
        }*/

    }

    @MainThread
    private fun callback() {

    }


}