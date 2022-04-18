package wee.digital.library.extension

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import wee.digital.library.app
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.reflect.KClass

fun Closeable.safeClose() {
    try {
        close()
    } catch (ignored: Exception) {
    }
}

val packageDir: File
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        app.getExternalFilesDir(null)!!
    } else {
        @Suppress("DEPRECATION")
        Environment.getExternalStorageDirectory()
    }

val externalPath: String get() = packageDir.absolutePath

fun packageDir(dir: String, fileName: String? = null): File {
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

fun publicDir(dir: String, fileName: String? = null): File {
    val parent = Environment.getExternalStoragePublicDirectory(dir)
    if (fileName != null) {
        return File(parent, fileName)
    }
    return parent
}

fun readBytesFromAssets(fileName: String): ByteArray? {
    return try {
        val inputStream = app.assets.open(fileName)
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        inputStream.close()
        return bytes
    } catch (e: FileNotFoundException) {
        null
    }
}

fun readAssets(filename: String): String? {
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

fun <T : Any> readAssets(fileName: String, cls: KClass<T>): T? {
    return readAssets(fileName).parse(cls)
}

fun <T : Any> readAssets(fileName: String, cls: KClass<Array<T>>): List<T>? {
    return readAssets(fileName).parse(cls)
}

fun saveBitmap(fileName: String, bitmap: Bitmap) {
    var fOut: OutputStream?
    val file = File(externalPath, "$fileName.png")
    fOut = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
    fOut.flush()
    fOut.close()
    MediaStore.Images.Media.insertImage(
        app.contentResolver,
        file.absolutePath,
        file.name,
        file.name
    )
}

fun newFile(fileName: String): File {
    return File(packageDir, fileName)
}

fun open(file: File) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        app.startActivity(Intent.createChooser(intent, ""))
    } catch (e: Exception) {
    }
}

fun copyFile(fileName: String) {
    var inputStream: InputStream? = null
    var fos: FileOutputStream? = null
    try {
        inputStream = app.assets.open(fileName)
        fos = FileOutputStream("${packageDir.absolutePath}/$fileName")
        val buffer = ByteArray(1024)
        var read: Int = inputStream.read(buffer)
        while (read >= 0) {
            fos.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
    } catch (e: IOException) {
    }
    inputStream?.close()
    fos?.flush()
    fos?.close()
}

fun createFileIfNotExist(fileName: String) {
    val dir = File(packageDir.absolutePath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(packageDir, fileName)
    if (file.exists()) return
    copyFile(fileName)
}

fun getFile(fileName: String): File {
    return File(packageDir, fileName)
}

fun isExistFile(fileName: String): Boolean {
    return File(packageDir, fileName).exists()
}

fun createFile(fileName: String) {
    val dir = File(externalPath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = getFile(fileName)
    if (file.exists()) {
        file.delete()
        getFile(fileName)
    }
    copyFile(fileName)
}

@RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun writeFile(fileName: String, bytes: ByteArray): File? {
    return try {
        val file = File(packageDir, fileName)
        val stream = FileOutputStream(file)
        stream.write(bytes)
        stream.flush()
        stream.close()
        file
    } catch (e: Exception) {
        null
    }
}

@RequiresPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
fun readFile(fileName: String): String {
    val file = File(packageDir, fileName)
    val text = java.lang.StringBuilder()
    try {
        val br = BufferedReader(FileReader(file))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            text.append(line)
            text.append('\n')
        }
        br.close()
    } catch (e: IOException) {
    }
    return text.toString()
}

@Throws(IOException::class)
fun unzip(zipFile: File?, targetDirectory: File?) {
    val zis = ZipInputStream(
        BufferedInputStream(FileInputStream(zipFile))
    )
    var ze: ZipEntry
    var count: Int
    val buffer = ByteArray(8192)
    while (zis.nextEntry.also { ze = it } != null) {
        val file = File(targetDirectory, ze.name)
        val dir = if (ze.isDirectory) file else file.parentFile
        if (!dir.isDirectory && !dir.mkdirs()) {
            toast("Failed to ensure directory: ${dir.absolutePath}")
            return
        }
        if (ze.isDirectory) continue
        val fos = FileOutputStream(file)
        while (zis.read(buffer).also { count = it } != -1) {
            fos.write(buffer, 0, count)
        }
    }
}

