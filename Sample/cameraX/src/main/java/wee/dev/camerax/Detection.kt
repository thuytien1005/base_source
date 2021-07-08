package wee.dev.camerax

import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import java.util.concurrent.Executors

class Detection {


    private var processing = false

    private var countGetFace = 0

    private val faceDetection = FaceDetection.getClient(highAccuracyOpts)

    private val executorService = Executors.newSingleThreadExecutor()

    var listener: DetectionCallBack? = null

    fun bitmapChecking(frame: ByteArray, width: Int, height: Int) {
        if (processing) return
        processing = true
        executorService.submit {
            val image =
                InputImage.fromByteArray(frame, width, height, 3, InputImage.IMAGE_FORMAT_NV21)
            faceDetection.process(image)
                .addOnSuccessListener {
                    executorService.submit {
                        val face = it.largestMlKitFace()
                        if (face == null) {
                            resetDetect()
                        } else {
                            val frameByte = BitmapUtils.NV21toJPEG(frame, width, height, 80)
                            optionFace(face, frameByte)
                        }
                    }
                }
                .addOnFailureListener {
                    resetDetect()
                }
        }
    }

    private fun optionFace(face: Face, frame: ByteArray) {
        if (!face.checkFaceOke()) {
            resetDetect()
            return
        }
        val bitmap = frame.toBitmap()
        val dataFace = bitmap.getDataFaceAndFace(face)
        if (dataFace.dataFace != null && dataFace.face != null) {
            countGetFace++
            if (countGetFace >= 3) {
                countGetFace = 0
                listener?.faceEligible(dataFace.face, dataFace.dataFace)
            }
            processing = false
        } else {
            resetDetect()
        }
    }

    private fun resetDetect() {
        processing = false
        countGetFace = 0
        listener?.faceNull()
    }

    fun destroyThread() {
        faceDetection.close()
        executorService.shutdownNow()
    }

    interface DetectionCallBack {
        fun faceNull() {}
        fun hasFace() {}
        fun faceEligible(bm: ByteArray, faceData: FacePointData)
    }
}