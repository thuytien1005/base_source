package wee.digital.camera.ui

import android.content.Context
import android.util.AttributeSet
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import wee.digital.camera.BuildConfig
import wee.digital.camera.CameraController
import wee.digital.camera.R
import wee.digital.camera.databinding.FaceCaptureLayoutBinding
import wee.digital.camera.face.FaceData
import wee.digital.camera.face.FaceDetector
import wee.digital.camera.face.FaceDetectorCallback
import kotlinx.coroutines.CoroutineScope

class FaceCaptureLayout : ConstraintLayout{

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    lateinit var bind: FaceCaptureLayoutBinding

    var onFaceCaptured: ((FaceData) -> Unit)? = null

    private lateinit var faceDetector: FaceDetector

    private lateinit var camera: CameraController

    private val faceScanLayout get() = bind.includeFaceScanLayout

    /**
     *
     */
    fun onViewCreated(fragment: Fragment) {
        bind = FaceCaptureLayoutBinding.bind(this)
        configCameraAndFaceDetector(fragment)
        if (BuildConfig.DEBUG) {
            show(bind.textViewResume, bind.imageViewFaceBytes, bind.imageViewFrameBytes)
        }
        faceScanLayout.imageViewPlus.animateAlpha()
        bind.textViewResume.setOnClickListener { startFaceDetect() }
    }

    private fun configCameraAndFaceDetector(fragment: Fragment) {
        camera = CameraController(object : CameraController.Interface {
            override fun cameraPreviewView(): PreviewView {
                return bind.previewView
            }

            override fun onImageAnalysis(image: ImageProxy) {
                faceDetector.detect(image)
            }

        })

        faceDetector = FaceDetector(object : FaceDetectorCallback {
            override fun detectorLifecycleScope(): CoroutineScope {
                return fragment.lifecycleScope
            }

            override fun detectorNoFace() {
                faceScanLayout.motionLayoutScanWidgets.hide()
            }

            override fun detectorHasFace() {
                faceScanLayout.motionLayoutScanWidgets.show()
            }

            override fun detectorFaceInvalid(s: String) {
                faceScanLayout.motionLayoutScanXY.animateHide {
                    faceScanLayout.motionLayoutScanXY.setTransition(
                        R.id.faceScanBegin,
                        R.id.faceScanStart
                    )
                }
            }

            override fun detectorEligibleFace(faceData: FaceData) {
                //faceDetector.pause()
                faceScanLayout.motionLayoutScanXY.addTransitionListener(object :
                    SimpleMotionTransitionListener {
                    override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
                        if (currentId == R.id.faceScanCompleted) {
                            bind.imageViewFrameBytes.setImageBitmap(faceData.image)
                            bind.imageViewFaceBytes.setImageBitmap(faceData.portrait)
                            onFaceCaptured?.invoke(faceData)
                        }
                    }
                })
                faceScanLayout.motionLayoutScanXY.transitionToState(R.id.faceScanStart)
            }
        })

        fragment.observerCameraPermission {
            startFaceDetect()
            camera.start(fragment.viewLifecycleOwner)
        }
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                faceDetector.pause()
                camera.stop()
            }
        })
    }

    fun startFaceDetect() {
        bind.imageViewFrameBytes.clear()
        bind.imageViewFaceBytes.clear()
        faceDetector.start()
    }

}