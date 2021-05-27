package wee.digital.ml.face

import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import wee.digital.ml.base.FaceDetectorLogger
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.base.ImageProcessor

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Sample
 * @Created: Huy 2021/05/19
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class FaceDetectorProcessor : ImageProcessor<List<Face>>() {

    private val log = FaceDetectorLogger()

    private val detector: FaceDetector = FaceDetection.getClient(faceOptions.detectorOptions)

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay) {
        faces.firstOrNull()?.also {
            graphicOverlay.add(FaceGraphic(graphicOverlay, it))
            log.d(it)
        }
    }

    override fun onFailure(e: Exception) {
        log.e(e)
    }

}