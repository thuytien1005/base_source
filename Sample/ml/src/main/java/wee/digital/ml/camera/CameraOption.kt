package wee.digital.ml.camera

import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import wee.digital.ml.ML
import wee.digital.ml.toast

object CameraOption {

    var lensFacing = CameraSelector.LENS_FACING_FRONT

    val newLensFacing
        get() = if (lensFacing == CameraSelector.LENS_FACING_FRONT)
            CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT

    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT

    var cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

    fun switchLensFacing(provider: ProcessCameraProvider): Boolean {
        val newCameraSelector = CameraSelector.Builder()
                .requireLensFacing(newLensFacing)
                .build()
        try {
            if (provider.hasCamera(newCameraSelector)) {
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                return true
            }
        } catch (ignore: CameraInfoUnavailableException) {
        }
        toast("This device does not have lens with facing: $newLensFacing")
        return false
    }

    val cameraProviderLiveData: LiveData<ProcessCameraProvider> by lazy {
        MutableLiveData<ProcessCameraProvider>().also {
            val cameraProvider = ProcessCameraProvider.getInstance(ML.app)
            cameraProvider.addListener(Runnable {
                try {
                    it.setValue(cameraProvider.get())
                } catch (e: Exception) {
                }
            }, ContextCompat.getMainExecutor(ML.app))
        }
    }

    val cameraProvider get() = cameraProviderLiveData.value

}