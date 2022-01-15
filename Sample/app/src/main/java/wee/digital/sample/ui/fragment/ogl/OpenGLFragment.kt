package wee.digital.sample.ui.fragment.ogl

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.mediapipe.components.TextureFrameConsumer
import com.google.mediapipe.framework.TextureFrame
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import wee.digital.library.extension.lightSystemWidgets
import wee.digital.library.extension.windowFullScreen
import wee.digital.sample.databinding.OglBinding
import wee.digital.sample.ui.main.MainFragment


class OpenGLFragment : MainFragment<OglBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return OglBinding::inflate
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
    private var glSurfaceView: SolutionGlSurfaceView<FaceMeshResult>? = null

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
        faceMesh!!.setErrorListener { message: String, e: RuntimeException? ->
            log.e(message)
        }

        cameraInput = CameraInput(requireActivity())
        cameraInput!!.setNewFrameListener(TextureFrameConsumer { textureFrame: TextureFrame? ->
            faceMesh!!.send(textureFrame)
        })

        // Initializes a new Gl surface view with a user-defined FaceMeshResultGlRenderer.
        glSurfaceView = SolutionGlSurfaceView(requireContext(), faceMesh!!.glContext, faceMesh!!.glMajorVersion)
        glSurfaceView!!.setSolutionResultRenderer(OpenGLRender())
        glSurfaceView!!.setRenderInputImage(true)

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