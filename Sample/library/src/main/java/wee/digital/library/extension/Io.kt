package wee.digital.library.extension

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wee.digital.library.app
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

private val packagePath get() = "Android/data/${app.packageName}/data"

val externalDir: File
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        File(app.getExternalFilesDir(null)!!, packagePath)
    } else {
        @Suppress("DEPRECATION")
        File(Environment.getExternalStorageDirectory(), packagePath)
    }

val externalPath: String get() = externalDir.absolutePath

val downloadDir: File get() = fileDir(Environment.DIRECTORY_DOWNLOADS)

fun fileDir(dir: String, fileName: String? = null): File {
    val parent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        app.getExternalFilesDir(dir)!!
    } else {
        @Suppress("DEPRECATION")
        Environment.getExternalStoragePublicDirectory(dir)
    }
    if (fileName != null) {
        return File(parent, fileName)
    }
    return parent
}

fun readStringFromAssets(filename: String): String? {
    return try {
        val sb = StringBuilder()
        BufferedReader(InputStreamReader(app.assets.open(filename))).useLines { lines ->
            lines.forEach {
                sb.append(it)
            }
        }
        return sb.toString()
    } catch (e: FileNotFoundException) {
        null
    }
}

fun download(file: File, url: String, onCompleted: (File) -> Unit) {
    val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (file.exists()) {
                onCompleted(file)
            } else {
                toast("Không thể tải tệp về thiết bị")
            }
            app.unregisterReceiver(this)
        }
    }
    app.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    val request = DownloadManager.Request(Uri.parse(url))
    request.setDestinationUri(Uri.fromFile(file))
    val manager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    GlobalScope.launch {
        manager.enqueue(request)
    }
}

fun downloadIfNotExist(file: File, url: String, onCompleted: (File) -> Unit) {
    if (file.exists()) {
        onCompleted(file)
    } else {
        download(file, url, onCompleted)
    }
}




