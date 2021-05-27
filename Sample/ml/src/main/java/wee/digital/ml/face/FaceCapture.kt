package wee.digital.ml.face

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import wee.digital.ml.ML
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.base.Logger
import wee.digital.ml.camera.cameraOptions
import wee.digital.ml.camera.hasCameraPermission

class FaceCapture(private val view: ViewInterface) :
        LifecycleObserver {

    interface ViewInterface {
        val lifecycleOwner: LifecycleOwner
        val cameraProvider: ProcessCameraProvider
        val previewView: PreviewView
        val graphicOverlay: GraphicOverlay
    }

    val log = Logger("FaceCapture")


    /**
     * [LifecycleObserver] implements
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onLifecycleEventStart() {
        log.d("onLifecycleEventStart")
        view.previewView.display.getRealMetrics(cameraOptions.metrics)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifecycleEventResume() {
        log.d("onLifecycleEventResume")
        if (hasCameraPermission) {
            bindPreview()
            bindImageAnalysis()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecycleEventPause() {
        log.d("onLifecycleEventPause")
        unBindPreview()
        unBindImageAnalysis()
    }

    /**
     * [Preview] use case
     */
    private val preview: Preview = cameraOptions.preview

    fun bindPreview() {
        view.cameraProvider.unbind(preview)
        preview.previewSurfaceProvider = view.previewView.previewSurfaceProvider
        view.cameraProvider.bindToLifecycle(view.lifecycleOwner, cameraOptions.selector, preview)
    }

    fun unBindPreview() {
        view.cameraProvider.unbind(preview)
    }

    /**
     * [ImageAnalysis] use case
     */
    val imageAnalysis = ImageAnalysis.Builder().build()

    val faceDetectorProcessor = FaceDetector()

    private var needUpdateGraphicOverlayImageSourceInfo = true

    fun bindImageAnalysis() {
        view.cameraProvider.unbind(imageAnalysis)
        imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(ML.app),
                { imageProxy: ImageProxy ->
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        when (imageProxy.imageInfo.rotationDegrees) {
                            0, 180 -> view.graphicOverlay.setImageSourceInfo(imageProxy.width, imageProxy.height, cameraOptions.isImageFlipped)
                            else -> view.graphicOverlay.setImageSourceInfo(imageProxy.height, imageProxy.width, cameraOptions.isImageFlipped)
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false
                    }
                    faceDetectorProcessor.process(imageProxy, view.graphicOverlay)
                }
        )
        view.cameraProvider.bindToLifecycle(
                view.lifecycleOwner,
                cameraOptions.selector,
                imageAnalysis
        )
    }

    fun unBindImageAnalysis() {
        faceDetectorProcessor.stopProcess()
    }

    init {
        view.lifecycleOwner.lifecycle.addObserver(this)
    }

}