package wee.digital.sample.ui.fragment.ogl

import android.opengl.GLSurfaceView
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import wee.digital.sample.databinding.OglBinding
import wee.digital.sample.ui.main.MainFragment


class OpenGLFragment : MainFragment<OglBinding>() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return OglBinding::inflate
    }

    override fun onViewCreated() {
        glSurfaceView = MyGLSurfaceView(requireContext())
        vb.layoutSurfaceView.apply {
            addView(glSurfaceView)
            requestLayout()
        }
    }

    /**
     *
     */

}