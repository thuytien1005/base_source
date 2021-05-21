package wee.digital.ml.ui

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.mlkit.common.MlKitException
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.base.VisionImageProcessor
import wee.digital.ml.camera.CameraOption
import wee.digital.ml.camera.CameraUtil
import wee.digital.ml.face.FaceDetectorProcessor
import wee.digital.ml.face.FaceOption


class FaceDetector(private val fdi: FaceDetectorInterface) {

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                bindAllUseCases()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                imageProcessor?.stop()
            }
        })
    }

    /**
     * Interface call
     */
    private val cameraProvider: ProcessCameraProvider get() = fdi.cameraProvider()

    private val previewView: PreviewView get() = fdi.previewView()

    private val graphicOverlay: GraphicOverlay get() = fdi.graphicOverlay()

    private val lifecycleOwner: LifecycleOwner get() = fdi.lifecycleOwner()

    /**
     * bind/unbind use case
     */
    fun unBindUseCase(useCase: UseCase?) {
        useCase ?: return
        cameraProvider.unbind(useCase)
    }

    fun unBindAllUseCases() {
        cameraProvider.unbindAll()
    }

    fun bindAllUseCases() {
        if (!CameraUtil.hasPermission) return
        cameraProvider.unbindAll()
        bindPreviewUseCase()
    }


    /**
     * Preview use case
     */
    private var previewUseCase: Preview? = null

    fun bindPreviewUseCase() {
        unBindUseCase(previewUseCase)
        previewUseCase = CameraOption.previewView
        previewUseCase?.previewSurfaceProvider = previewView.previewSurfaceProvider
        cameraProvider?.bindToLifecycle(lifecycleOwner, CameraOption.selector, previewUseCase)
    }

    /**
     * Image analysis use case
     */
    private var analysisUseCase: ImageAnalysis? = null

    private var imageProcessor: VisionImageProcessor? = null

    private var needUpdateGraphicOverlayImageSourceInfo = false

    fun bindAnalysisUseCase(context: Context) {
        imageProcessor?.stop()
        imageProcessor = FaceDetectorProcessor(context, FaceOption.highAccuracyOpts)
        val builder = ImageAnalysis.Builder()
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase?.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(context),
                ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, CameraOption.isImageFlipped)
                        } else {
                            graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, CameraOption.isImageFlipped)
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false
                    }
                    try {
                        imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
                    } catch (e: MlKitException) {

                    }
                }
        )
        cameraProvider?.bindToLifecycle(lifecycleOwner, CameraOption.selector, analysisUseCase)
    }


}