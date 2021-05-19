package wee.digital.ml.ui

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * View model for interacting with CameraX.
 * Create an instance which interacts with the camera service via the given application context.
 */
class CameraXViewModel(application: Application) : AndroidViewModel(application) {

    val processCameraProvider: LiveData<ProcessCameraProvider> by lazy {
        MutableLiveData<ProcessCameraProvider>().also {
            val cameraProvider = ProcessCameraProvider.getInstance(getApplication())
            cameraProvider.addListener({
                try {
                    it.setValue(cameraProvider.get())
                } catch (e: Exception) {
                }
            }, ContextCompat.getMainExecutor(getApplication()))
        }
    }

}