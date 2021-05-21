package wee.digital.ml.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import wee.digital.ml.ML

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Sample
 * @Created: Huy 2021/05/21
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
object CameraUtil {

    val hasPermission: Boolean
        get() = ContextCompat.checkSelfPermission(ML.app, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    fun onPermissionGranted(activity: Activity, block: () -> Unit) {
        if (hasPermission) {
            block()
        } else {
            requestPermission(activity)
        }
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
    }


    fun switchLensFacing(provider: ProcessCameraProvider): Boolean {
        val newCameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraOption.newLensFacing)
                .build()
        try {
            if (provider.hasCamera(newCameraSelector)) {
                CameraOption.lensFacing = CameraOption.newLensFacing
                CameraOption.selector = newCameraSelector
                return true
            }
        } catch (ignore: CameraInfoUnavailableException) {
        }
        return false
    }

    val cameraProviderLiveData = object : MutableLiveData<ProcessCameraProvider>() {
        init {
            val cameraProvider = ProcessCameraProvider.getInstance(ML.app)
            cameraProvider.addListener({
                try {
                    value = cameraProvider.get()
                } catch (e: Exception) {
                }
            }, ContextCompat.getMainExecutor(ML.app))
        }
    }
}