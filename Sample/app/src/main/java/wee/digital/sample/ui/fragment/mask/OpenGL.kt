package wee.digital.sample.ui.fragment.mask

import android.graphics.Color
import android.opengl.GLES20
import androidx.annotation.ColorInt

fun loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)
    return shader
}

fun floatArrayColor(@ColorInt color: Int): FloatArray {
    return floatArrayOf(
        Color.red(color) / 255f,
        Color.green(color) / 255f,
        Color.blue(color) / 255f,
        Color.alpha(color) / 255f
    )
}