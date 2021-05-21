package wee.digital.ml.camera

import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview

object CameraOption {

    var lensFacing: Int = CameraSelector.LENS_FACING_FRONT

    var selector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

    val resolution: Size
        get() = Size(720, 1280)

    val aspectRatio: Int
        get() = AspectRatio.RATIO_4_3

    val newLensFacing
        get() = if (lensFacing == CameraSelector.LENS_FACING_FRONT)
            CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT

    val isImageFlipped: Boolean
        get() = lensFacing == CameraSelector.LENS_FACING_FRONT

    val flashMode: Int
        get() = ImageCapture.FLASH_MODE_AUTO

    val imageCapture: ImageCapture
        get() = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(flashMode)
                .setTargetAspectRatio(aspectRatio)
                .build()

    val previewView: Preview
        get() = Preview.Builder()
                //.setTargetAspectRatio(AspectRatio.RATIO_4_3)
                //.setTargetResolution(resolution)
                .build()

}