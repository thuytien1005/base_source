package wee.digital.camera

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import wee.digital.camera.util.Logger
import wee.digital.camera.util.OpenCV

object CameraApp {

    fun init(application: Application) {
        mApp = application
        OpenCV.initLoader(application)
    }

}

private var mApp: Application? = null

val app: Application get() = mApp ?: throw NullPointerException("Module not init")

val log by lazy { Logger("library") }

private var toast: Toast? = null

fun toast(message: String?) {
    message ?: return
    onMain {
        kotlin.runCatching {
            toast?.cancel()
            toast = Toast.makeText(app.applicationContext, message, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}

fun onMain(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) block()
    else Handler(Looper.getMainLooper()).post { block() }
}

fun onMain(delay: Long, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ block() }, delay)
}