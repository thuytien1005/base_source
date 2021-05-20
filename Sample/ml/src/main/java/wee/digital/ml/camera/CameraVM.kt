package wee.digital.ml.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import wee.digital.ml.ML
import wee.digital.ml.toast

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Sample
 * @Created: Huy 2021/05/20
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class CameraVM : ViewModel() {

    companion object {

        fun get(owner: ViewModelStoreOwner): CameraVM {
            val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(ML.app)
            return ViewModelProvider(owner, factory).get(CameraVM::class.java)
        }
    }

    val provider: ProcessCameraProvider? = null

    var lensFacing = CameraSelector.LENS_FACING_FRONT

    val newLensFacing
        get() = if (lensFacing == CameraSelector.LENS_FACING_FRONT)
            CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT

    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT

    var selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

    val imageCapture = ImageCapture.Builder()
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()

    private var previewUseCase: Preview? = null

    val hasPermission: Boolean
        get() = ContextCompat.checkSelfPermission(ML.app, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    val providerLiveData: LiveData<ProcessCameraProvider> by lazy {
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

    fun bindPreviewUseCase(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        if (!hasPermission) return
        provider?.safeUnbind(previewUseCase)
        previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.previewSurfaceProvider = previewView.previewSurfaceProvider
                }
        provider?.bindToLifecycle(lifecycleOwner, selector, previewUseCase)
    }

    fun switchLensFacing(provider: ProcessCameraProvider): Boolean {
        val newCameraSelector = CameraSelector.Builder()
                .requireLensFacing(newLensFacing)
                .build()
        try {
            if (provider.hasCamera(newCameraSelector)) {
                lensFacing = newLensFacing
                selector = newCameraSelector
                return true
            }
        } catch (ignore: CameraInfoUnavailableException) {
        }
        toast("This device does not have lens with facing: $newLensFacing")
        return false
    }

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

    private fun ProcessCameraProvider?.safeUnbind(userCase: UseCase?) {
        userCase ?: return
        this?.unbind(userCase)
    }
}