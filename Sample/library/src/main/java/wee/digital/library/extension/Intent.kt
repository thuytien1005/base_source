package wee.digital.library.extension

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import wee.digital.library.app
import kotlin.reflect.KClass


fun <T : Activity> Fragment.start(cls: KClass<T>) {
    requireActivity().start(cls)
}

fun <T : Activity> Activity.start(cls: KClass<T>) {
    startActivity(Intent(this, cls.java))
}

fun navigateSettings(intentString: String?) {
    val intent = Intent(intentString)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    app.startActivity(intent)
}

fun navigateSettings() {
    navigateSettings(Settings.ACTION_SETTINGS)
}

fun navigateLocationSettings() {
    navigateSettings(Settings.ACTION_LOCALE_SETTINGS)
}

fun navigateSecuritySettings() {
    navigateSettings(Settings.ACTION_SECURITY_SETTINGS)
}

fun navigateWifiSettings() {
    navigateSettings(Settings.ACTION_WIFI_SETTINGS)
}

fun navigateBluetoothSettings() {
    navigateSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
}

fun navigateEmail() {
    app.startActivity(Intent(Intent.ACTION_MAIN).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        it.addCategory(Intent.CATEGORY_APP_EMAIL)
    })
}

fun navigateCHPlay() {
    try {
        app.startActivity(Intent(Intent.ACTION_VIEW).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.data = Uri.parse("market://details?id=${app.packageName}")
        })
    } catch (ex: android.content.ActivityNotFoundException) {
        app.startActivity(Intent(Intent.ACTION_VIEW).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.data = Uri.parse("https://play.google.com/store/apps/details?id=${app.packageName}")
        })
    }
}

fun navigateBrowser(url: String) {
    app.startActivity(Intent(Intent.ACTION_VIEW).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        it.data = Uri.parse(url)
    })
}

fun navigateCall(phone: String) {
    app.startActivity(Intent(Intent.ACTION_DIAL).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        it.data = Uri.parse("tel:$phone")
    })
}

fun navigateDateSettings() {
    navigateSettings(Settings.ACTION_DATE_SETTINGS)
}

fun navigateAppSettings() {
    app.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        it.data = Uri.fromParts("package", app.packageName, null)
    })
}

fun realPathFromURI(uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val cursor: Cursor = app.contentResolver.query(uri, projection, null, null, null)
        ?: return uri.path
    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
    cursor.moveToFirst()
    return cursor.getString(columnIndex)
}


