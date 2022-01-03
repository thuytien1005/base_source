package com.example.camera.ui

import android.content.Context
import android.util.AttributeSet
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.camera.BuildConfig
import com.example.camera.CameraController
import com.example.camera.R
import com.example.camera.databinding.FaceCaptureLayoutBinding
import com.example.camera.face.FaceData
import com.example.camera.face.FaceDetectorCallback
import com.example.camera.face.FaceDetectorProcessor
import com.example.camera.toBitmap

class FaceCaptureLayout : ConstraintLayout,
    CameraController.Interface,
    FaceDetectorCallback {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    lateinit var bind: FaceCaptureLayoutBinding

    var onFaceCaptured: ((FaceData) -> Unit)? = null

    private lateinit var faceDetector: FaceDetectorProcessor

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
        camera = CameraController(this)
        faceDetector = FaceDetectorProcessor(fragment.lifecycleScope, this)
        fragment.observerCameraPermission {
            startFaceDetect()
            camera.start(fragment.viewLifecycleOwner)
        }
        fragment.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy() {
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

    /**
     * [CameraController.Interface]
     */
    override fun cameraPreviewView(): PreviewView {
        return bind.previewView
    }

    override fun onImageAnalysis(image: ImageProxy) {
        faceDetector.onProcess(image)
    }

    /**
     * [FaceDetectorCallback] implements
     */
    override fun detectedHasFace() {
        faceScanLayout.motionLayoutScanWidgets.show()
    }

    override fun detectedNoFace() {
        faceScanLayout.motionLayoutScanWidgets.hide()
    }

    override fun detectedHadEligible(faceData: FaceData) {
        //faceDetector.pause()
        faceScanLayout.motionLayoutScanXY.addTransitionListener(object :
            SimpleMotionTransitionListener {
            override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
                if (currentId == R.id.faceScanCompleted) {
                    bind.imageViewFrameBytes.setImageBitmap(faceData.frameBytes.toBitmap())
                    bind.imageViewFaceBytes.setImageBitmap(faceData.faceBytes.toBitmap())
                    onFaceCaptured?.invoke(faceData)
                }
            }
        })
        faceScanLayout.motionLayoutScanXY.transitionToState(R.id.faceScanStart)
    }

    override fun detectedNoEligible() {
        faceScanLayout.motionLayoutScanXY.animateHide {
            faceScanLayout.motionLayoutScanXY.setTransition(R.id.faceScanBegin, R.id.faceScanStart)
        }
    }

}