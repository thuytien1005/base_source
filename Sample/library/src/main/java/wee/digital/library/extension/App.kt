package wee.digital.library.extension

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import wee.digital.library.app

private val memoryInfo = ActivityManager.MemoryInfo()

val freeMemory: Long
    get() {
        val manager = app.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        manager.getMemoryInfo(memoryInfo)
        return (memoryInfo.availMem) / (1024 * 1024)
    }

val appVersion: String
    get() {
        return try {
            app.packageManager.getPackageInfo(app.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            return "v1.0"
        }
    }

val packageName: String get() = app.applicationContext.packageName

val packageUrl: String get() = "package:$packageName"

val isTablet: Boolean
    get() {
        return app.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

var currentToast: Toast? = null

fun toast(message: String?) {
    message ?: return
    onUi {
        currentToast = Toast.makeText(app.applicationContext, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}

fun toast(@StringRes res: Int?, vararg arguments: Any) {
    res ?: return
    val message = try {
        app.resources.getString(res, *arguments)
    } catch (ex: Resources.NotFoundException) {
        return
    }
    toast(message)
}

fun restartApp() {
    val intent = app.packageManager.getLaunchIntentForPackage(packageName)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    app.startActivity(intent)
}