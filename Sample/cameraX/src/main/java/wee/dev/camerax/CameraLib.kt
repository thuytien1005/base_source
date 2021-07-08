package wee.dev.camerax

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Size
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import wee.dev.camerax.databinding.LayoutCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraLib(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val bd = LayoutCameraBinding.inflate(LayoutInflater.from(context), this, true)

    private var displayId: Int = -1

    private var lensFacing: Int = Config.LENS_FRONT

    private var cameraProvider: ProcessCameraProvider? = null

    private var preview: Preview? = null

    private var imageAnalyzer: ImageAnalysis? = null

    private var camera: Camera? = null

    private var imageCapture: ImageCapture? = null

    private var detection: Detection? = null

    private var cameraExecutor: ExecutorService? = null

    private val life = context as LifecycleOwner

    fun createCamera(listener: Detection.DetectionCallBack) {
        detection = Detection()
        detection?.listener = listener
    }

    fun resumeCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        bd.myCameraXPreview.post {
            displayId = bd.myCameraXPreview.display.displayId
            setupCamera()
        }
    }

    fun pauseCamera() {
        cameraExecutor?.shutdown()
    }

    fun destroyCamera() {
        detection?.destroyThread()
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            if (!cameraProvider.hasBackCamera() && lensFacing == Config.LENS_BACK) {
                toast(string(R.string.error_lens_back))
                return@addListener
            }
            if (!cameraProvider.hasFrontCamera() && lensFacing == Config.LENS_FRONT) {
                toast(string(R.string.error_lens_front))
                return@addListener
            }
            bindCamera()
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("RestrictedApi")
    private fun bindCamera() {
        val metrics = DisplayMetrics().also {
            bd.myCameraXPreview.display.getRealMetrics(it)
        }
        val screenRatio = metrics.aSpecRatio()
        val rotation = bd.myCameraXPreview.display.rotation
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("camera initialization failed")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(screenRatio)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetAspectRatio(screenRatio)
            .setDefaultResolution(Size(Config.WITH_CAMERA, Config.HEIGHT_CAMERA))
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenRatio)
            .setTargetRotation(rotation)
            .setDefaultResolution(Size(Config.WITH_CAMERA, Config.HEIGHT_CAMERA))
            .build()
            .also {
                it.setAnalyzer(cameraExecutor!!, LuminosityAnalyzer { frame, with, height ->
                    detection?.bitmapChecking(frame, with, height)
                })
            }
        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(
                life,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(bd.myCameraXPreview.surfaceProvider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toast(mess: String) {
        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
    }

    private fun string(@StringRes id: Int): String {
        return context.getString(id)
    }

}