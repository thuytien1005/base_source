package wee.digital.ml.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.mlkit.common.MlKitException
import kotlinx.android.synthetic.main.face_live_preview.*
import wee.digital.ml.R
import wee.digital.ml.base.VisionImageProcessor
import wee.digital.ml.camera.CameraOption
import wee.digital.ml.face.DetectorOption
import wee.digital.ml.face.FaceDetectorProcessor
import wee.digital.ml.hasCameraPermission

class CameraXLivePreviewActivity :
        AppCompatActivity(),
        ActivityCompat.OnRequestPermissionsResultCallback {


    private val cameraProvider: ProcessCameraProvider? get() = CameraOption.cameraProvider
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_live_preview)
        CameraOption.cameraProviderLiveData.observe(this, Observer {
            bindAllCameraUseCases()
        })
    }


    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        imageProcessor?.run {
            this.stop()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run {
            this.stop()
        }
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null && hasCameraPermission) {
            cameraProvider?.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView.surfaceProvider)
        cameraProvider!!.bindToLifecycle(this, CameraOption.cameraSelector, previewUseCase)
    }

    private fun bindAnalysisUseCase() {

        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor?.stop()
        }
        imageProcessor = FaceDetectorProcessor(this, DetectorOption.highAccuracyOpts)

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
        cameraProvider?.bindToLifecycle(this, CameraOption.cameraSelector, analysisUseCase)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bindAllCameraUseCases()
    }

}
