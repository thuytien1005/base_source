package wee.digital.ml.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.face_auto_capture.*
import wee.digital.ml.R
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.camera.CameraUtil

class FaceAutoCaptureActivity : AppCompatActivity() {

    private var detector: FaceDetector? = null

    /**
     * [AppCompatActivity] implements
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_auto_capture)
        CameraUtil.requestPermission(this)
        CameraUtil.cameraProviderLiveData.observe(this, {
            it ?: return@observe
            detector?.unBindAllUseCases()
            detector = FaceDetector(object : FaceDetectorInterface {

                override fun lifecycleOwner(): LifecycleOwner = this@FaceAutoCaptureActivity

                override fun cameraProvider(): ProcessCameraProvider = it

                override fun previewView(): PreviewView = this@FaceAutoCaptureActivity.previewView

                override fun graphicOverlay(): GraphicOverlay = this@FaceAutoCaptureActivity.graphicOverlay
            })
        })
    }

}
