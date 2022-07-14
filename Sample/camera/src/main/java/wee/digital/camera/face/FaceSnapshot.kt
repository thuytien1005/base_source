package wee.digital.camera.face

import android.graphics.Bitmap
import wee.digital.camera.util.toByteArray
import kotlin.math.roundToInt

class FaceSnapshot {

    var dataPoint: FaceDataPoints? = null
    var faceBytes: ByteArray? = null
    var frameBytes: ByteArray? = null
    var face: com.google.mlkit.vision.face.Face? = null

    fun setFace(frameBitmap: Bitmap, face: com.google.mlkit.vision.face.Face) {
        this.face = face
        this.frameBytes = frameBitmap.toByteArray()
        val rect = face.getRectRatio()

        val extraH = 0.2f
        val extraW = 0.2f

        val plusH = rect.height() * extraH
        val plusW = rect.width() * extraW

        val height = rect.height() + plusH.roundToInt()
        val width = rect.width() + plusW.roundToInt()
        val top = rect.top - (plusH / 2).roundToInt()
        val left = rect.left - (plusW / 2).roundToInt()

        val copiedBitmap = frameBitmap.copy(Bitmap.Config.ARGB_8888, true)
        try {
            val faceBitmap = Bitmap.createBitmap(
                copiedBitmap,
                left,
                top,
                width,
                height
            )
            dataPoint =
                face.getDataFace((rect.width() * .2f).toInt(), (rect.height() * .2f).toInt())
            faceBytes = faceBitmap.toByteArray()
            frameBitmap.recycle()
            faceBitmap?.recycle()
        } catch (ex: Exception) {
            dataPoint = face.getDataPoint()
            faceBytes = frameBitmap.toByteArray()
            frameBitmap.recycle()
        }
    }
}