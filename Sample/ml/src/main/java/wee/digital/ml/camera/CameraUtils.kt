package wee.digital.ml.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
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
val hasCameraPermission: Boolean
    get() = ContextCompat.checkSelfPermission(ML.app, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

private var cameraPermissionObserver: LifecycleObserver? = null

fun onCameraPermissionGranted(activity: AppCompatActivity, onGranted: () -> Unit) {
    if (hasCameraPermission) {
        onGranted()
        return
    }
    val observerPermission = {
        cameraPermissionObserver?.also {
            activity.lifecycle.removeObserver(it)
        }
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (hasCameraPermission) {
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


