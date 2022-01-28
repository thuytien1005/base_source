package wee.digital.sample.ui.fragment.face.reg

import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.mediapipe.components.TextureFrameConsumer
import com.google.mediapipe.framework.TextureFrame
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import wee.digital.library.extension.lightSystemWidgets
import wee.digital.library.extension.windowFullScreen
import wee.digital.sample.databinding.FaceRegBinding
import wee.digital.sample.ui.main.MainFragment


class FaceRegFragment : MainFragment<FaceRegBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return FaceRegBinding::inflate
    }

    override fun onViewCreated() {
        lightSystemWidgets()
        windowFullScreen()
        observerCameraPermission {
            stopCurrentPipeline()
            setupStreamingModePipeline()
        }
    }

    override fun onResume() {
        super.onResume()
        restartCameraAndOpenGLRendering()
    }

    /**
     * [FaceMesh],[CameraInput]
     */
    private var faceMesh: FaceMesh? = null
    private var cameraInput: CameraInput? = null
    private var glSurfaceView: FaceRegSurfaceView? = null

    private fun restartCameraAndOpenGLRendering() {
        cameraInput = CameraInput(requireActivity())
        cameraInput!!.setNewFrameListener { textureFrame: TextureFrame? ->
            faceMesh!!.send(textureFrame)
        }
        glSurfaceView!!.post { startCamera() }
        glSurfaceView!!.visibility = View.VISIBLE

    }

    private fun setupStreamingModePipeline() {
        // Initializes a new MediaPipe Face Mesh solution instance in the streaming mode.
        val options = FaceMeshOptions.builder()
            .setStaticImageMode(false)
            .setRefineLandmarks(true)
            .setRunOnGpu(true) // true: run on GPU
            .build()

        faceMesh = FaceMesh(requireContext(), options)
        faceMesh!!.setErrorListener { message: String, _: RuntimeException? ->
            log.e(message)
        }

        cameraInput = CameraInput(requireActivity())
        cameraInput!!.setNewFrameListener(TextureFrameConsumer { textureFrame: TextureFrame? ->
            faceMesh!!.send(textureFrame)
        })

        // Initializes a new Gl surface view with a user-defined FaceMeshResultGlRenderer.
        glSurfaceView =
            FaceRegSurfaceView(requireContext(), faceMesh!!.glContext, faceMesh!!.glMajorVersion)
        glSurfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                val render = FaceRegRender()
                //render.ratio = width.toFloat()/height
                glSurfaceView!!.setSolutionResultRenderer(render)
                glSurfaceView!!.setRenderInputImage(true)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })


        // listener FaceMeshResult and start camera
        faceMesh!!.setResultListener { faceMeshResult: FaceMeshResult? ->
            glSurfaceView!!.setRenderData(faceMeshResult)
            glSurfaceView!!.requestRender()
        }
        glSurfaceView!!.post { startCamera() }

        // Updates the preview layout.
        vb.layoutSurfaceView.apply {
            removeAllViewsInLayout()
            addView(glSurfaceView)
            glSurfaceView!!.visibility = View.VISIBLE
            requestLayout()
        }
    }

    private fun stopCurrentPipeline() {
        cameraInput?.setNewFrameListener(null)
        cameraInput?.close()
        glSurfaceView?.visibility = View.GONE
        faceMesh?.close()
    }

    private fun startCamera() {
        cameraInput!!.start(
            requireActivity(),
            faceMesh!!.glContext,
            CameraInput.CameraFacing.FRONT,
            glSurfaceView!!.width,
            glSurfaceView!!.height
        )
    }


}