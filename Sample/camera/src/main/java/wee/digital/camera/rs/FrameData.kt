package wee.digital.camera.rs

import android.graphics.Bitmap
import com.intel.realsense.librealsense.Extension
import com.intel.realsense.librealsense.Frame
import com.intel.realsense.librealsense.VideoFrame
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import wee.digital.camera.util.clone
import java.io.ByteArrayOutputStream

class FrameData {

    var bitmap: Bitmap? = null
    var mat: Mat? = null
    val bytes: ByteArray
        get() {
            bitmap ?: return byteArrayOf()
            return try {
                val stream = ByteArrayOutputStream()
                bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
                    ?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray = stream.toByteArray()
                stream.close()
                byteArray
            } catch (e: Exception) {
                byteArrayOf()
            }
        }

    val safeBitmap: Bitmap?
        get() {
            if (bitmap == null || bitmap!!.isRecycled) {
                bitmap = getBitmap(mat)
                return bitmap
            }
            return bitmap!!.clone()
        }

    constructor()

    constructor(frame: Frame?, getBitmap: Boolean = false) {
        this.mat = getMatRatio11(frame)
        if (getBitmap) {
            convertMatToBitmap()
        }
    }

    fun close() {
        bitmap?.recycle()
        mat?.release()
    }

    fun convertMatToBitmap() {
        if (bitmap == null) {
            bitmap = getBitmap(mat)
        }
    }

    companion object {

        fun getMatRatio11(frame: Frame?): Mat? {
            frame ?: return null
            try {
                val videoFrame: VideoFrame = frame.`as`(Extension.VIDEO_FRAME)
                val width = videoFrame.width
                val height = videoFrame.height
                val offsetX = width / 2 - height / 2
                val imageBytes = ByteArray(videoFrame.width * videoFrame.height * 3)
                videoFrame.getData(imageBytes)
                val originMat = Mat(height, width, CvType.CV_8UC3)
                originMat.put(0, 0, imageBytes)
                val cropMat: Mat = originMat.submat(
                    org.opencv.core.Rect(
                        offsetX,
                        0,
                        height,
                        height
                    )
                )
                Core.flip(cropMat, cropMat, 1) // Horizontal flip
                return cropMat
            } catch (e: Exception) {
            }
            return null
        }

        fun getBitmap(mat: Mat?): Bitmap? {
            mat ?: return null
            try {
                val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(mat, bitmap)
                return bitmap
            } catch (ignore: Exception) {
            }
            return null
        }
    }


}