package wee.digital.sample.ui.fragment.ogl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView : GLSurfaceView {

    private val renderer: MyGLRenderer

    constructor(context: Context?, attrs: AttributeSet?=null):  super(context, attrs){
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer()
        setRenderer(renderer)
    }
}