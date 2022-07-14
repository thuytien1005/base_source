package wee.digital.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import wee.digital.camera.util.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import wee.digital.camera.util.rotate
import wee.digital.camera.util.toBitmap
import java.util.concurrent.Executors

class CameraController(private val int: Interface) {

    interface Interface {
        fun lenFacing(): Int = CameraSelector.LENS_FACING_FRONT
        fun cameraPreviewView(): PreviewView
        fun onImageAnalysis(imageProxy: ImageProxy) = Unit
    }

    private val log = Logger("CameraController")
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val previewView get() = int.cameraPreviewView()
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvideJob: Job? = null

    fun start(lifecycleOwner: LifecycleOwner) {
        cameraProvideJob = lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (!isGranted(Manifest.permission.CAMERA)) return@launch
            cameraProvider = withContext(Dispatchers.IO) { ProcessCameraProvider.getInstance(app).get() }
            if (!cameraProvider.cameraFront() && !cameraProvider.cameraBack()) {
                toast("Không tìm thấy camera")
                return@launch
            }
            camera = initCamera(lifecycleOwner)
        }
    }

    fun stop() {
        cameraProvideJob?.cancel()
        cameraProvider?.unbindAll()
    }

    @SuppressLint("RestrictedApi", "WrongConstant")
    fun initCamera(lifecycleOwner: LifecycleOwner): Camera? {
        log.d("init camera")
        val size = Size(640, 480)
        val aspectRatio = AspectRatio.RATIO_4_3
        val rotation = previewView.display.rotation

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(int.lenFacing())
            .build()
        preview = Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .setDefaultResolution(size)
            .build()
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetAspectRatio(aspectRatio)
            .setDefaultResolution(size)
            .build()

        /**
         * [androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888]
         * [androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888]
         */
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setDefaultResolution(size)
            .build().also {

                it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    /*graphicOverlay.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        int.lenFacing() == CameraSelector.LENS_FACING_FRONT
                    )*/
                    int.onImageAnalysis(imageProxy)
                }
            }
        cameraProvider?.unbindAll()
        return try {
            log.d("start camera")
            preview?.setSurfaceProvider(previewView.surfaceProvider)
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception) {
            null
        }
    }

    fun takePictureFlow(): Flow<Bitmap> = callbackFlow {
        val listenerCapture = object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exception: ImageCaptureException) {
                close(exception)
            }

            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                val rotation = image.imageInfo.rotationDegrees
                val bitmap = image.image?.toBitmap().rotate(rotation)
                if (bitmap == null) {
                    close(NullPointerException("bitmap is null"))
                } else {
                    offer(bitmap)
                }
                image.close()
            }
        }
        imageCapture?.takePicture(Executors.newSingleThreadExecutor(), listenerCapture)
        awaitClose { cancel() }
    }

    fun isGranted(vararg permissions: String): Boolean {
        permissions.iterator().forEach {
            if (ContextCompat.checkSelfPermission(app, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}