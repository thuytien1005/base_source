package wee.digital.ml.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.face_auto_capture.*
import wee.digital.ml.R
import wee.digital.ml.base.GraphicOverlay
import wee.digital.ml.camera.onCameraPermissionGranted
import wee.digital.ml.face.FaceCapture

class FaceAutoCaptureActivity : AppCompatActivity() {

    private var detector: FaceCapture? = null

    /**
     * [AppCompatActivity] implements
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_auto_capture)
        onCameraPermissionGranted(this) {
            val cameraProvider = ProcessCameraProvider.getInstance(this)
            cameraProvider.addListener({
                detector?.unBindPreview()
                detector?.unBindImageAnalysis()
                detector = FaceCapture(object : FaceCapture.ViewInterface {

                    override val lifecycleOwner: LifecycleOwner
                        get() = this@FaceAutoCaptureActivity

                    override val cameraProvider: ProcessCameraProvider
                        get() = cameraProvider.get()

                    override val previewView: PreviewView
                        get() = this@FaceAutoCaptureActivity.previewView

                    override val graphicOverlay: GraphicOverlay
                        get() = this@FaceAutoCaptureActivity.graphicOverlay
                })


            }, ContextCompat.getMainExecutor(this))

        }

    }


}
