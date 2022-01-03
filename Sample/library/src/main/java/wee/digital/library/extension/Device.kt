package wee.digital.library.extension

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import wee.digital.library.app
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*


val macAddress: String
    get() {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (e: Exception) {
        }
        return ""
    }

val androidId: String
    get() {
        return Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
    }

val deviceCode: String
    get() {
        return "${Build.VERSION.RELEASE}"
    }

val nowInUTC: String
    @SuppressLint("SimpleDateFormat")
    get() {
        val time = Build.TIME
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getTimeZone("UTC")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = cal.timeZone
        return sdf.format(Date(time))
    }

const val deviceOs = "android"

val deviceBrand: String = Build.MANUFACTURER

val manager: PackageManager = app.packageManager

val info: PackageInfo = manager.getPackageInfo(app.packageName, 0)