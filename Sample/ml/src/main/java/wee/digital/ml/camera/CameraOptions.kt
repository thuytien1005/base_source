package wee.digital.ml.camera

import android.util.DisplayMetrics
import android.util.Rational
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider

val cameraOptions by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    CameraOptions()
}

class CameraOptions {

    /**
     * Option
     */
    var targetResolution: Size = Size(1200, 1200)

    var aspectRatio: Int = AspectRatio.RATIO_4_3

    var lensFacing: Int = CameraSelector.LENS_FACING_FRONT

    var flashMode: Int = ImageCapture.FLASH_MODE_AUTO

    var selector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

    var metrics: DisplayMetrics = DisplayMetrics()

    /**
     *
     */
    val screenSize get() = Size(metrics.widthPixels, metrics.heightPixels)

    val screenAspectRatio get() = Rational(metrics.widthPixels, metrics.heightPixels)

    val newLensFacing
        get() = if (lensFacing == CameraSelector.LENS_FACING_FRONT)
            CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT

    val isImageFlipped: Boolean
        get() = lensFacing == CameraSelector.LENS_FACING_FRONT


    val imageCapture: ImageCapture
        get() = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(flashMode)
                .setTargetAspectRatio(aspectRatio)
                .build()

    val preview: Preview
        get() = Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .build()


    fun switchLensFacing(provider: ProcessCameraProvider): Boolean {
        val newCameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraOptions.newLensFacing)
                .build()
        try {
            if (provider.hasCamera(newCameraSelector)) {
                cameraOptions.lensFacing = cameraOptions.newLensFacing
                cameraOptions.selector = newCameraSelector
                return true
            }
        } catch (ignore: CameraInfoUnavailableException) {
        }
        return false
    }


}