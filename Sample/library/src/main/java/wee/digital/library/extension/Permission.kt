package wee.digital.library.extension

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import wee.digital.library.app

fun isGranted(vararg permissions: String): Boolean {
    permissions.iterator().forEach {
        if (ContextCompat.checkSelfPermission(app, it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

/**
 *
 */
fun LifecycleOwner.onGrantedPermission(
    vararg permissions: String,
    onGranted: () -> Unit,
    onDenied: (List<String>) -> Unit
) {
    val deniedPermissions = mutableListOf<String>()
    val notGrantedPermissions = mutableListOf<String>()
    val activity = requireActivity() ?: return
    for (permission in permissions) {
        when {
            isGranted(permission) -> {
                continue
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                deniedPermissions.add(permission)
            }
            else -> {
                notGrantedPermissions.add(permission)
            }
        }
    }
    if (notGrantedPermissions.isNotEmpty()) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                lifecycle.removeObserver(this)
                if (isGranted(*permissions)) {
                    onGranted.invoke()
                }
            }
        })
        ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), 101)
        return
    }
    if (deniedPermissions.isNotEmpty()) {
        onDenied(deniedPermissions.toList())
        return
    }
    onGranted.invoke()
}

fun LifecycleOwner.observerPermission(
    vararg permissions: String,
    onGranted: () -> Unit,
    onDenied: (List<String>) -> Unit
) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            if (isGranted(*permissions)) {
                onGranted.invoke()
            }
        }
    })
    if (!isGranted(*permissions)) {
        onGrantedPermission(permissions = permissions, onGranted = { }, onDenied = onDenied)
    }
}







