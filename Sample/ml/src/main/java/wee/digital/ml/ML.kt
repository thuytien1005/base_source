package wee.digital.ml

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass

object ML {

    /**
     * Module must be set on create application
     */
    private var mApp: Application? = null

    var app: Application
        set(value) {
            mApp = value
        }
        get() {
            if (null == mApp) throw NullPointerException("module not be set")
            return mApp!!
        }

}

fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): T {
    val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(ML.app)
    return ViewModelProvider(this, factory).get(cls.java)
}

val hasCameraPermission: Boolean get() = ContextCompat.checkSelfPermission(ML.app, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

fun onCameraPermissionGranted(block: () -> Unit) {
    val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            block()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        }
    }
    TedPermission.with(ML.app)
            .setPermissionListener(permissionListener)
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
}

fun toast(message: String?) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        Toast.makeText(ML.app, message.toString(), Toast.LENGTH_SHORT).show()
    } else Handler(Looper.getMainLooper()).post {
        Toast.makeText(ML.app, message.toString(), Toast.LENGTH_SHORT).show()
    }
}

private var curBrightness = 0f

fun Activity.screenBrightness(@FloatRange(from = 0.0, to = 1.0) brightness: Float) {
    this.let {
        val attr = it.window.attributes
        curBrightness = attr.screenBrightness
        attr.screenBrightness = brightness
        it.window.attributes = attr
    }
}

fun Activity.rollbackScreenBrightness() {
    this.let {
        val attr = it.window.attributes
        attr.screenBrightness = curBrightness
        it.window.attributes = attr
    }
}

fun str(@StringRes res: Int): String? {
    return try {
        ML.app.getString(res)
    } catch (e: Resources.NotFoundException) {
        null
    }
}

@ColorInt
fun color(@ColorRes res: Int): Int {
    return try {
        ContextCompat.getColor(ML.app, res)
    } catch (e: Resources.NotFoundException) {
        Color.BLACK
    }
}

/**
 * [android.util.Base64] encode
 */
fun Bitmap.toBytes(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    return outputStream.toByteArray()
}

fun ByteArray.encodeToString(flag: Int = Base64.NO_WRAP): String {
    return Base64.encodeToString(this, flag)
}

fun Bitmap.toBase64String(format: Int = Base64.NO_WRAP): String {
    return toBytes().encodeToString(format)
}

/**
 * [android.util.Base64] decode
 */
fun String.decodeToBytes(flag: Int = Base64.NO_WRAP): ByteArray {
    return Base64.decode(this, flag)
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

fun String.toBitmap(flag: Int = Base64.NO_WRAP): Bitmap {
    return decodeToBytes(flag).toBitmap()
}

