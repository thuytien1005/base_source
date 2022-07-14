package wee.digital.camera.util

import android.graphics.*
import android.media.Image
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

fun Bitmap?.clone(): Bitmap? {
    this ?: return null
    return this.copy(this.config, true)
}

fun ByteArray?.nv21ToBitmap(width: Int, height: Int): Bitmap? {
    this ?: return null
    val out = ByteArrayOutputStream()
    val yuv = YuvImage(this, ImageFormat.NV21, width, height, null)
    yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val bytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Bitmap.toByteArray(): ByteArray {
    return try {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        stream.close()
        byteArray
    } catch (e: Exception) {
        byteArrayOf()
    }
}

fun ByteArray?.toBitmap(): Bitmap? {
    this ?: return null
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

fun Bitmap?.rotate(degrees: Int): Bitmap? {
    this ?: return null
    val matrix = Matrix()
    matrix.postRotate(degrees.toFloat())
    matrix.postScale(-1f, 1f)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Image.yuv420toNV21(): ByteArray {
    val crop = this.cropRect
    val format = this.format
    val width = crop.width()
    val height = crop.height()
    val planes = this.planes
    val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
    val rowData = ByteArray(planes[0].rowStride)
    var channelOffset = 0
    var outputStride = 1
    for (i in planes.indices) {
        when (i) {
            0 -> {
                channelOffset = 0
                outputStride = 1
            }
            1 -> {
                channelOffset = width * height + 1
                outputStride = 2
            }
            2 -> {
                channelOffset = width * height
                outputStride = 2
            }
        }
        val buffer = planes[i].buffer
        val rowStride = planes[i].rowStride
        val pixelStride = planes[i].pixelStride
        val shift = if (i == 0) 0 else 1
        val w = width shr shift
        val h = height shr shift
        buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
        for (row in 0 until h) {
            var length: Int
            if (pixelStride == 1 && outputStride == 1) {
                length = w
                buffer[data, channelOffset, length]
                channelOffset += length
            } else {
                length = (w - 1) * pixelStride + 1
                buffer[rowData, 0, length]
                for (col in 0 until w) {
                    data[channelOffset] = rowData[col * pixelStride]
                    channelOffset += outputStride
                }
            }
            if (row < h - 1) {
                buffer.position(buffer.position() + rowStride - length)
            }
        }
    }
    return data
}

fun Image.yuv420toBitmap(): Bitmap? {
    return this.yuv420toNV21().nv21ToBitmap(width, height)
}

fun Image.rgba8888ToBitmap(): Bitmap? {
    val planes = this.planes
    val buffer: ByteBuffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride
    val rowPadding = rowStride - pixelStride * this.width
    val bitmap = Bitmap.createBitmap(
        this.width + rowPadding / pixelStride,
        this.height, Bitmap.Config.ARGB_8888
    )
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}