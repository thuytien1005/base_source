package wee.digital.ml.base

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import androidx.annotation.GuardedBy
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import java.nio.ByteBuffer
import java.util.*

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(VisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class ImageProcessorBackup<T> {

    private val now get() = SystemClock.elapsedRealtime()

    private var infoCallback: ImageProcessorInfoCallback? = ImageProcessorInfoCallback()

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    // Whether this processor is already shut down
    private var isShutdown = false

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetadata? = null

    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    /**
     * Code for processing single still image
     */
    fun processBitmap(bitmap: Bitmap, graphicOverlay: GraphicOverlay) {
        val frameStartTime = now
        val image: InputImage = InputImage.fromBitmap(bitmap, 0)
        requestDetectInImage(
                inputImage = image,
                graphicOverlay = graphicOverlay,
                originalCameraImage = null,
                frameStartTime = frameStartTime
        )
    }

    /**
     * Code for processing live preview frame from CameraX API
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay) {
        if (isShutdown) return
        try {
            val frameStartTime = now

            @SuppressLint("UnsafeExperimentalUsageError")
            val bitmap = BitmapUtils.getBitmap(image)

            @SuppressLint("UnsafeExperimentalUsageError")
            val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

            requestDetectInImage(
                    inputImage = inputImage,
                    graphicOverlay = graphicOverlay,
                    originalCameraImage = bitmap,
                    frameStartTime = frameStartTime
            )
                    .addOnCompleteListener {
                        // When the image is from CameraX analysis use case, must call image.close() on received
                        // images when finished using them. Otherwise, new images may not be received or the camera
                        // may stall.
                        image.close()
                    }
        } catch (e: Exception) {

        }

    }

    /**
     * Code for processing live preview frame from Camera1 API
     */
    private fun processImage(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {
        val frameStartTime = now
        val bitmap = BitmapUtils.getBitmap(data, frameMetadata)
        val image: InputImage = InputImage.fromByteBuffer(
                data,
                frameMetadata.width,
                frameMetadata.height,
                frameMetadata.rotation,
                InputImage.IMAGE_FORMAT_NV21
        )
        requestDetectInImage(
                inputImage = image,
                graphicOverlay = graphicOverlay,
                originalCameraImage = bitmap,
                frameStartTime = frameStartTime
        ).addOnSuccessListener(executor) {
            processLatestImage(graphicOverlay)
        }
    }

    @Synchronized
    private fun processLatestImage(graphicOverlay: GraphicOverlay) {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage!!, processingMetaData!!, graphicOverlay)
        }
    }

    @Synchronized
    fun processByteBuffer(data: ByteBuffer?, frameMetadata: FrameMetadata?, graphicOverlay: GraphicOverlay) {
        latestImage = data
        latestImageMetaData = frameMetadata
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(graphicOverlay)
        }
    }


    /**
     * Common processing logic
     */
    private fun requestDetectInImage(
            inputImage: InputImage,
            graphicOverlay: GraphicOverlay,
            originalCameraImage: Bitmap?,
            frameStartTime: Long,
    ): Task<T> {
        val detectorStartTime = now
        return detectInImage(inputImage)
                .addOnSuccessListener(executor) { results: T ->
                    infoCallback?.onProcessInfo(frameStartTime, detectorStartTime)
                    graphicOverlay.clear()
                    if (originalCameraImage != null) {
                        graphicOverlay.add(CameraImageGraphic(graphicOverlay, originalCameraImage))
                    }
                    this@ImageProcessorBackup.onSuccess(results, graphicOverlay)
                    graphicOverlay.postInvalidate()
                }
                .addOnFailureListener(executor) { e: Exception ->
                    graphicOverlay.clear()
                    graphicOverlay.postInvalidate()
                    e.printStackTrace()
                    this@ImageProcessorBackup.onFailure(e)
                }
    }

    open fun stop() {
        executor.shutdown()
        isShutdown = true
        infoCallback?.cancel()
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)

    protected abstract fun onFailure(e: Exception)

}