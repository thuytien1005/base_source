package wee.digital.sample.ui.fragment.face.reg

import android.content.Context
import android.view.SurfaceHolder
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import javax.microedition.khronos.egl.EGLContext


class FaceRegSurfaceView : SolutionGlSurfaceView<FaceMeshResult> {


    constructor(context: Context, eglContext: EGLContext, version: Int) : super(
        context,
        eglContext,
        version
    ) {

    }


}