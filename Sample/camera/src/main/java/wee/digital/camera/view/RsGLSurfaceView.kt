package wee.digital.camera.view

import android.content.Context
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Pair
import android.view.MotionEvent
import com.intel.realsense.librealsense.Frame
import com.intel.realsense.librealsense.FrameSet
import com.intel.realsense.librealsense.GLRenderer

class RsGLSurfaceView : GLSurfaceView, AutoCloseable {

    private var renderer: GLRenderer? = null
    private var previousX = 0.0f
    private var previousY = 0.0f

    constructor(context: Context?, attrs: AttributeSet? = null) : super(context, attrs) {
        renderer = GLRenderer()
        setRenderer(renderer)
    }

    val rectangles: Map<Int, Pair<String, Rect>>
        get() = renderer!!.rectangles

    fun upload(frames: FrameSet?) {
        renderer?.upload(frames)
    }

    fun upload(frame: Frame?) {
        renderer?.upload(frame)
    }

    fun clear() {
        renderer?.clear()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y
        return when (e.action) {
            2 -> {
                val dx = x - previousX
                val dy = y - previousY
                renderer?.onTouchEvent(dx, dy)
                previousX = x
                previousY = y
                true
            }
            else -> {
                previousX = x
                previousY = y
                true
            }
        }
    }

    fun showPointCloud(showPoints: Boolean) {
        renderer?.showPointcloud(showPoints)
    }

    override fun close() {
        renderer?.close()
    }

}