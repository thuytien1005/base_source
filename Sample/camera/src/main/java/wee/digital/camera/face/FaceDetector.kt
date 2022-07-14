package wee.digital.camera.face

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import androidx.camera.core.ImageProxy
import wee.digital.camera.util.Logger
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.*
import wee.digital.camera.util.nv21ToBitmap
import wee.digital.camera.util.rotate
import wee.digital.camera.util.yuv420toNV21

class FaceDetector(private val callback: FaceDetectorCallback) {

    companion object {
        val mlKitFaceDetector by lazy {
            val options = FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setMinFaceSize(0.5f)
                .build()
            FaceDetection.getClient(options)
        }
    }

    class ProcessException(message: String? = null) : IllegalArgumentException(message)

    class NoFaceException(message: String? = null) : NullPointerException(message)

    class InvalidFaceException(message: String? = null) : IllegalArgumentException(message)

    private val log = Logger("faceDetector")

    private var currentFaceId: Int = -1

    private var isProcessing: Boolean = false
    private var isDetecting: Boolean = false

    private var noFaceCount: Int = 0
    private var hasFaceCount: Int = 0
    private var eligibleFaceCount: Int = 0

    private var faceDetectJob: Job? = null

    fun start() {
        isProcessing = false
        isDetecting = true
        hasFaceCount = 0
        noFaceCount = 0
        eligibleFaceCount = 0
        callback.detectorNoFace()
    }

    fun pause() {
        isDetecting = false
        faceDetectJob?.cancel()
    }

    private fun onMain(block: suspend CoroutineScope.() -> Unit) {
        callback.detectorLifecycleScope().launch(Dispatchers.Main, CoroutineStart.DEFAULT, block)
    }

    fun detect(imageProxy: ImageProxy) {
        if (isProcessing || !isDetecting) {
            imageProxy.close()
            return
        }
        isProcessing = true
        faceDetectJob?.cancel()
        faceDetectJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val faceList = getFace(imageProxy)
                faceFilter(faceList)
                onEligibleFace(imageProxy) {
                    val faceData = FaceData(it, it)
                    callback.detectorEligibleFace(faceData)
                }
            } catch (e: Exception) {
                onFaceException(e)
            }
            isProcessing = false
            imageProxy.close()
        }
    }

    private fun getFace(imageBytes: ByteArray, width: Int, height: Int, rotate: Int): List<Face> {
        val visionImage = InputImage
            .fromByteArray(imageBytes, width, height, rotate, ImageFormat.NV21)
        return Tasks.await(mlKitFaceDetector.process(visionImage))
    }

    private fun getFace(imageProxy: ImageProxy): List<Face> {
        @SuppressLint("UnsafeOptInUsageError")
        val image: Image = imageProxy.image ?: return listOf()
        val visionImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        return Tasks.await(mlKitFaceDetector.process(visionImage))
    }

    private fun faceFilter(faceList: List<Face>) {
        val faceSet = FaceSet(faceList)
        if (faceSet.largeFace == null) {
            throw NoFaceException()
        }
        if (faceSet.largeFace?.trackingId != currentFaceId) {
            currentFaceId = faceSet.largeFace?.trackingId ?: -1
            throw NoFaceException()
        }
        if (faceSet.smallFace != null) {
            throw InvalidFaceException("Có nhiều hơn 1 bản mặt")
        }

        onHasFace()
        val face = faceSet.largeFace!!
        if (!face.checkHeadEuler()) {
            throw InvalidFaceException("Giữ đầu thẳng")
        }
        if (!face.boundingBox.checkFaceCenter()) {
            throw InvalidFaceException("Đưa bản mặt vào giữa")
        }
    }

    private fun Face.checkHeadEuler(): Boolean {
        val x = this.headEulerAngleX
        val y = this.headEulerAngleY
        return x in -15f..15f && y in -15f..15f
    }

    private fun Rect.checkFaceCenter(): Boolean {
        return try {
            val x = this.exactCenterX()
            val y = this.exactCenterY()
            log.d("checkZoneFaceMlKit : x $x - y $y")
            x in 100f..380f
        } catch (e: java.lang.Exception) {
            false
        }
    }

    /**
     *
     */
    private fun onFaceException(e: Exception?) {
        if (!isDetecting) return
        when (e) {
            is ProcessException -> {
                log.d("processException: ${e.message}")
                return
            }
            is NoFaceException -> {
                log.d("noFaceException: ${e.message}")
                hasFaceCount = 0
                noFaceCount = 0
                eligibleFaceCount = 0
                log.d("eligibleCount (NoFaceException): $eligibleFaceCount")
                noFaceCount++
                if (noFaceCount > 5) onMain {
                    noFaceCount = 0
                    callback.detectorNoFace()
                }
            }
            is InvalidFaceException -> {
                log.d("invalidFaceException: ${e.message}")
                eligibleFaceCount = 0
                log.d("eligibleCount (InvalidFaceException): $eligibleFaceCount")
                onMain {
                    callback.detectorFaceInvalid(
                        e.message ?: "Đưa khuôn mặt bạn vào giữa vùng nhận diện"
                    )
                }
            }
        }
    }

    private fun onHasFace() {
        if (!isDetecting) return
        log.d("onHasFace")
        noFaceCount = 0
        hasFaceCount++
        if (hasFaceCount > 1) onMain {
            hasFaceCount = 0
            callback.detectorHasFace()
        }
    }

    private fun onEligibleFace(imageProxy: ImageProxy, onEligible: (Bitmap) -> Unit) {
        if (!isDetecting) return
        eligibleFaceCount++
        log.d("eligibleCount: $eligibleFaceCount")
        if (eligibleFaceCount < 4) {
            return
        }
        try {
            isDetecting = false
            @SuppressLint("UnsafeOptInUsageError")
            val image: Image = imageProxy.image ?: return
            val visionImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val bitmap: Bitmap = image.yuv420toNV21()
                .nv21ToBitmap(imageProxy.width, imageProxy.height)
                .rotate(visionImage.rotationDegrees) ?: throw NoFaceException()
            onMain { onEligible(bitmap) }
        } catch (e: Exception) {

        }
    }


}