package wee.digital.ml.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.face_auto_capture.*
import wee.digital.ml.R
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.camera.CameraUtil

class FaceAutoCaptureActivity : AppCompatActivity(){

    private var detector: FaceDetector? = null

    /**
     * [AppCompatActivity] implements
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_auto_capture)
        CameraUtil.onPermissionGranted(this) {
                    CameraSelector.Builder().requireLensFacing( CameraSelector.LENS_FACING_FRONT).build()

           val cameraProvider = ProcessCameraProvider.getInstance(this)
            cameraProvider.addListener({
                detector?.unBindAllUseCases()
                detector = FaceDetector(object : FaceDetectorInterface {
                    override fun lifecycleOwner(): LifecycleOwner = this@FaceAutoCaptureActivity

                    override fun cameraProvider(): ProcessCameraProvider = cameraProvider.get()

                    override fun previewView(): PreviewView = this@FaceAutoCaptureActivity.previewView

                    override fun graphicOverlay(): GraphicOverlay = this@FaceAutoCaptureActivity.graphicOverlay
                })

            }, ContextCompat.getMainExecutor(this))

        }

    }


}
