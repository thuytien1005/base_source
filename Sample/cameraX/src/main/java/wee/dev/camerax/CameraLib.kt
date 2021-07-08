package wee.dev.camerax

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
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

    private var cameraExecutor: ExecutorService? = null

    private val life = context as LifecycleOwner

    private val main = Handler(Looper.getMainLooper())

    fun resumeCamera() {
        cameraExecutor = Executors.newSingleThreadScheduledExecutor()
        bd.cameraPreview.post {
            displayId = bd.cameraPreview.display.displayId
            setupCamera()
        }
    }

    @SuppressLint("RestrictedApi")
    fun pauseCamera() {
        cameraExecutor?.shutdownNow()
    }

    fun resetCamera() {
        bd.cameraPreview.show()
        bd.cameraResult.gone()
    }

    fun getImageCapture(block: (ByteArray?) -> Unit) {
        cameraExecutor ?: return
        imageCapture?.takePicture(
            cameraExecutor!!,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    cameraExecutor?.submit {
                        try {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
                            val bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            val bmRotate = BitmapUtils.rotate(bm, 270)
                            val byteRotation = BitmapUtils.bitmapToByteArray(bmRotate)
                            block(byteRotation)
                            main.post {
                                bd.cameraPreview.gone()
                                bd.cameraResult.show()
                                bd.cameraResult.setImageBitmap(bmRotate)
                            }
                        } catch (e: java.lang.Exception) {
                            block(null)
                        }
                    }
                }
            })
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            if (!cameraProvider.hasBackCamera() && lensFacing == Config.LENS_BACK) {
                print(string(R.string.error_lens_back))
                return@addListener
            }
            if (!cameraProvider.hasFrontCamera() && lensFacing == Config.LENS_FRONT) {
                print(string(R.string.error_lens_front))
                return@addListener
            }
            bindCamera()
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("RestrictedApi")
    private fun bindCamera() {
        val metrics = DisplayMetrics().also {
            bd.cameraPreview.display.getRealMetrics(it)
        }
        val screenRatio = metrics.aSpecRatio()
        val rotation = bd.cameraPreview.display.rotation
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

        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(
                life,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(bd.cameraPreview.surfaceProvider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun string(@StringRes id: Int): String {
        return context.getString(id)
    }

    private fun View.show() {
        main.post { this.visibility = View.VISIBLE }
    }

    private fun View.hide() {
        main.post { this.visibility = View.INVISIBLE }
    }

    private fun View.gone() {
        main.post { this.visibility = View.GONE }
    }
}