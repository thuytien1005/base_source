package wee.digital.ml.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
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

    private var permissionObserver: LifecycleObserver? = null

    fun onPermissionGranted(activity: ComponentActivity, onGranted: () -> Unit) {
        if (hasPermission) {
            onGranted()
            return
        }

        val observerPermission = {
            permissionObserver?.also {
                activity.lifecycle.removeObserver(it)
            }
            val observer = object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                fun onResume() {
                    if (hasPermission) {
                        activity.lifecycle.removeObserver(this)
                        onGranted()
                    }
                }
            }
            activity.lifecycle.addObserver(observer)
        }
        when {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) -> {
                observerPermission()
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
            }
            else -> {
                observerPermission()
                AlertDialog.Builder(activity)
                        .setMessage("camera permission require")
                        .setPositiveButton("Close") { dialog, _ -> dialog.cancel() }
                        .setNegativeButton("Setting") { dialog, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            activity.startActivity(intent)
                            dialog.cancel()
                        }
                        .show()
            }
        }

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

    val cameraProviderLiveData = MutableLiveData<ProcessCameraProvider>()


}