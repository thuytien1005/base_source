package wee.digital.ml.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.mlkit.common.MlKitException
import kotlinx.android.synthetic.main.face_live_preview.*
import wee.digital.ml.R
import wee.digital.ml.base.VisionImageProcessor
import wee.digital.ml.camera.CameraVM
import wee.digital.ml.face.Detector
import wee.digital.ml.face.FaceDetectorProcessor

class CameraXLivePreviewActivity :
        AppCompatActivity() {

    private val camVM  : CameraVM by lazy { CameraVM.get(this) }

    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_live_preview)
        camVM.requestPermission(this)
        camVM.providerLiveData.observe(this, Observer {
            bindAllCameraUseCases()
        })

    }

    override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        imageProcessor?.run {
            this.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run {
            this.stop()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bindAllCameraUseCases()
    }

    private fun bindAllCameraUseCases() {
        if (camVM.hasPermission) {
            camVM.bindPreviewUseCase(this, previewView)
            bindAnalysisUseCase()
        }
    }

    private fun bindAnalysisUseCase() {
        if (analysisUseCase != null) {
            camVM.provider?.unbind(analysisUseCase)
        }
        imageProcessor?.stop()
        imageProcessor = FaceDetectorProcessor(this, Detector.highAccuracyOpts)

        val builder = ImageAnalysis.Builder()
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase?.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this),
                ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, camVM.isImageFlipped)
                        } else {
                            graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, camVM.isImageFlipped)
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false
                    }
                    try {
                        imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
                    } catch (e: MlKitException) {

                    }
                }
        )
        camVM.provider?.bindToLifecycle(this, camVM.selector, analysisUseCase)
    }


}
