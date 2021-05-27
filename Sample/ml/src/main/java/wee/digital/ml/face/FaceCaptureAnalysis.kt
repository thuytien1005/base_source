package wee.digital.ml.face

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import wee.digital.ml.ML
import wee.digital.ml.camera.cameraOptions

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Sample
 * @Created: Huy 2021/05/27
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class FaceCaptureAnalysis(private val view: FaceCapture.ViewInterface) {

    private val faceImageAnalysis = ImageAnalysis.Builder().build()

    private val faceDetectorProcessor = FaceDetectorProcessor()

    private var needUpdateGraphicOverlayImageSourceInfo = true

    fun bindUseCase() {
        view.cameraProvider.unbind(faceImageAnalysis)
        faceImageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(ML.app),
                { imageProxy: ImageProxy ->
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        when (imageProxy.imageInfo.rotationDegrees) {
                            0, 180 -> view.graphicOverlay.setImageSourceInfo(imageProxy.width, imageProxy.height, cameraOptions.isImageFlipped)
                            else -> view.graphicOverlay.setImageSourceInfo(imageProxy.height, imageProxy.width, cameraOptions.isImageFlipped)
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false
                    }
                    faceDetectorProcessor.processImageProxy(imageProxy, view.graphicOverlay)
                }
        )
        view.cameraProvider.bindToLifecycle(
                view.lifecycleOwner,
                cameraOptions.selector,
                faceImageAnalysis
        )
    }

    fun unBindUseCase() {
        faceDetectorProcessor.stop()
    }

}