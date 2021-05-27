package wee.digital.ml.face

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
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

    private val analysisUseCase by lazy { FaceCaptureAnalysis(view) }

    private val previewUseCase by lazy { FaceCapturePreview(view) }

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
            previewUseCase.bindUseCase()
            analysisUseCase.bindUseCase()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecycleEventPause() {
        log.d("onLifecycleEventPause")
        unbindUseCases()
    }

    fun unbindUseCases() {
        previewUseCase.bindUseCase()
        analysisUseCase.unBindUseCase()
    }

    init {
        view.lifecycleOwner.lifecycle.addObserver(this)
    }

}