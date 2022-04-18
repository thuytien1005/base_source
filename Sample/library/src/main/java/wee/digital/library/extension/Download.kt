package wee.digital.library.extension

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import wee.digital.library.app
import java.io.File

val downloadManager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

val downloadDir: File get() = publicDir(Environment.DIRECTORY_DOWNLOADS)

interface DownloadListener {
    fun onSuccessful(file: File)
    fun onCancelled() = Unit
}

/**
 * Download file by system download service
 * can cancel download by
 * @return downloadId
 */

fun Activity.download(url: String, file: File, listener: DownloadListener): Long {
    val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
            if (downloadId < 0) {
                unregisterReceiver(this)
                return
            }
            try {
                val cursor: Cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                if (!cursor.moveToFirst()) {
                    listener.onCancelled()
                    return
                }
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusIndex)
                if (status != DownloadManager.STATUS_SUCCESSFUL) {
                    listener.onCancelled()
                    return
                }
                val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val uri = cursor.getString(uriIndex)!!
                listener.onSuccessful(File(Uri.parse(uri).path!!))
            } catch (e: Exception) {
                listener.onCancelled()
            }
            unregisterReceiver(this)
        }
    }
    registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    val request = DownloadManager.Request(Uri.parse(url))
    request.setDestinationUri(Uri.fromFile(file))
    return downloadManager.enqueue(request)
}

fun Activity.download(url: String, fileName: String, listener: DownloadListener): Long {
    return download(url, File(packageDir, fileName), listener)
}

fun Activity.downloadIfNotExist(url: String, file: File, listener: DownloadListener): Long {
    if (file.exists()) {
        listener?.onSuccessful(file)
        return -1
    }
    return download(url, file, listener)

}

fun Activity.downloadIfNotExist(url: String, fileName: String, listener: DownloadListener): Long {
    return downloadIfNotExist(url, File(packageDir, fileName), listener)
}