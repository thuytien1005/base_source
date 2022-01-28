package wee.digital.sample.ui.fragment.face

import android.view.LayoutInflater
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.camera.CameraController
import com.example.camera.face.FaceData
import com.example.camera.face.FaceDetectorCallback
import com.example.camera.face.FaceDetectorProcessor
import com.example.camera.toBitmap
import com.example.camera.ui.FaceScanView
import wee.digital.sample.databinding.FaceBinding
import wee.digital.sample.ui.main.MainFragment


class FaceMLFragment : MainFragment<FaceBinding>(),
    CameraController.Interface,
    FaceDetectorCallback,
    FaceScanView {

    private lateinit var faceDetector: FaceDetectorProcessor

    private lateinit var camera: CameraController

    private val faceScanLayout get() = vb.includeFaceScanLayout

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return FaceBinding::inflate
    }

    override fun onViewCreated() {
        configCameraAndFaceDetector(fragment)
        vb.textViewResume.setOnClickListener { startFaceDetect() }
        faceScanLayout.onViewCreated()
    }

    override fun onLiveDataObserve() {

    }

    private fun configCameraAndFaceDetector(fragment: Fragment) {
        camera = CameraController(this)
        faceDetector = FaceDetectorProcessor(fragment.lifecycleScope, this)
        observerCameraPermission {
            startFaceDetect()
            camera.start(fragment.viewLifecycleOwner)
        }
        addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                faceDetector.pause()
                camera.stop()
            }
        })
    }

    fun startFaceDetect() {
        vb.imageViewFrameBytes.setImageDrawable(null)
        vb.imageViewFaceBytes.setImageDrawable(null)
        faceDetector.start()
    }

    /**
     * [CameraController.Interface]
     */
    override fun cameraPreviewView(): PreviewView {
        return vb.previewView
    }

    override fun onImageAnalysis(image: ImageProxy) {
        faceDetector.onProcess(image)
    }

    /**
     * [FaceDetectorCallback] implements
     */
    override fun detectedHasFace() {
        faceScanLayout.show()
    }

    override fun detectedNoFace() {
        faceScanLayout.hide()
    }

    override fun detectedHadEligible(faceData: FaceData) {
        faceDetector.pause()
        faceScanLayout.animateCaptured {
            vb.imageViewFrameBytes.setImageBitmap(faceData.frameBytes.toBitmap())
            vb.imageViewFaceBytes.setImageBitmap(faceData.faceBytes.toBitmap())
            // on face captured
        }
    }

    override fun detectedNoEligible() {
        faceScanLayout.animateHide {

        }
    }


}