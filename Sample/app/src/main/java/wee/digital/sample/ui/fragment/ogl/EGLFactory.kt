package wee.digital.sample.ui.fragment.ogl

import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import wee.digital.sample.app
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

private class EGLFactory : GLSurfaceView.EGLContextFactory {

    override fun createContext(egl: EGL10, display: EGLDisplay, eglConfig: EGLConfig): EGLContext {
        println("creating OpenGL ES $glVersion context")
        return egl.eglCreateContext(
            display,
            eglConfig,
            EGL10.EGL_NO_CONTEXT,
            intArrayOf(EGL_CONTEXT_CLIENT_VERSION, glVersion.toInt(), EGL10.EGL_NONE)
        ) // returns null if 3.0 is not supported
    }

    override fun destroyContext(egl: EGL10?, display: EGLDisplay?, context: EGLContext?) {
    }

    /**
     * Check the OpenGL ES version by creating a minimum supported context first,
     * and then checking the version string:
     */
    fun checkVersion() {
        /*egl.glGetString(GL10.GL_VERSION).also {
            println("Version: $it")
        }*/
    }

    companion object {
        const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        const val glVersion = 3.0
        var deviceSupportsAEP: Boolean =
            app.packageManager.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
    }
}


