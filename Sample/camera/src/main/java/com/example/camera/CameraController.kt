package com.example.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.util.DisplayMetrics
import android.util.Size
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.camera.util.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraController(private val int: Interface) {

    interface Interface {

        fun cameraPreviewView(): PreviewView

        fun onImageAnalysis(image: ImageProxy)
    }

    private val log = Logger("CameraController")

    private var camera: Camera? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private val previewView get() = int.cameraPreviewView()

    private var preview: Preview? = null

    private var imageCapture: ImageCapture? = null

    private var imageAnalysis: ImageAnalysis? = null

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun start(lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(app)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            if (!cameraProvider.cameraFront() && !cameraProvider.cameraBack()) {
                AlertDialog.Builder(previewView.context as Activity)
                    .setMessage("Không tìm lấy camera ")
                    .setPositiveButton("Đóng") { dialog, _ -> dialog.cancel() }
                    .show()
                return@Runnable
            }
            camera = initCamera(lifecycleOwner)
        }, ContextCompat.getMainExecutor(app))
    }

    fun stop() {
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }

    @SuppressLint("RestrictedApi")
    fun initCamera(lifecycleOwner: LifecycleOwner): Camera? {
        log.d("init camera")
        val metrics = DisplayMetrics().also { previewView.display?.getRealMetrics(it) }
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val size = Size(width, height)
        val aspectRatio = AspectRatio.RATIO_4_3
        val rotation = previewView.display.rotation

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
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
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .setDefaultResolution(size)
            .build().also {
                it.setAnalyzer(cameraExecutor, { imageProxy ->
                    int.onImageAnalysis(imageProxy)
                })
            }
        cameraProvider?.unbindAll()
        return try {
            log.d("start camera")
            preview?.setSurfaceProvider(previewView.surfaceProvider)
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis!!
            )
        } catch (e: Exception) {
            null
        }
    }

    fun imageCapture() {
        imageCapture?.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {

            }
        })
    }
}