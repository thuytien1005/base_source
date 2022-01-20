package wee.digital.sample.ui.fragment.face.mesh

import android.graphics.Color
import android.opengl.GLES20
import android.util.Log
import androidx.annotation.ColorInt
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

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

class FloatPoint(var x: Float, var y: Float, var radius: Float = 0F) {

    fun log() {
        val fmt = DecimalFormat("#.#####")
        Log.d("circle", "Centre (${fmt.format(x)},${fmt.format(y)})")
        Log.d("circle", "Radius $radius")
    }
}

fun getIrisCircle(
    landmarks: List<LandmarkProto.NormalizedLandmark>,
    connections: ImmutableSet<FaceMeshConnections.Connection>
): FloatPoint? {
    if (connections.size < 4) return null
    val list = mutableListOf<LandmarkProto.NormalizedLandmark>()
    for (conn in connections) {
        list.add(landmarks[conn.start()])
        if (list.size == 3) {
            return getIrisCircle(
                list[0].x.toDouble(), list[0].y.toDouble(),
                list[1].x.toDouble(), list[1].y.toDouble(),
                list[2].x.toDouble(), list[2].y.toDouble()
            )
        }
    }
    return null
}

fun getIrisCircle(
    x1: Double, y1: Double,
    x2: Double, y2: Double,
    x3: Double, y3: Double
): FloatPoint {
    val x12 = x1 - x2
    val x13 = x1 - x3
    val y12 = y1 - y2
    val y13 = y1 - y3
    val y31 = y3 - y1
    val y21 = y2 - y1
    val x31 = x3 - x1
    val x21 = x2 - x1

    // x1^2 - x3^2
    val sx13 = x1.pow(2.0) - x3.pow(2.0)

    // y1^2 - y3^2
    val sy13 = y1.pow(2.0) - y3.pow(2.0)
    val sx21 = x2.pow(2.0) - x1.pow(2.0)
    val sy21 = y2.pow(2.0) - y1.pow(2.0)
    val f =
        ((sx13 * x12 + sy13 * x12 + sx21 * x13 + sy21 * x13) / (2 * (y31 * x12 - y21 * x13)))
    val g =
        ((sx13 * y12 + sy13 * y12 + sx21 * y13 + sy21 * y13) / (2 * (x31 * y12 - x21 * y13)))
    val c = (-x1.pow(2.0)) - y1.pow(2.0) - 2 * g * x1 - 2 * f * y1

    // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
    // where centre is (h = -g, k = -f) and radius r
    // as r^2 = h^2 + k^2 - c
    val h = -g
    val k = -f
    val sqrOfRad = h * h + k * k - c

    // r is the radius
    val r = sqrt(sqrOfRad)

    val point = FloatPoint(h.toFloat(), k.toFloat(), r.toFloat())
    point.log()
    return point
}

fun getIrisCenter(
    landmarks: List<LandmarkProto.NormalizedLandmark>,
    connections: ImmutableSet<FaceMeshConnections.Connection>
): FloatPoint? {
    if (connections.size < 4) return null
    val list = mutableListOf<LandmarkProto.NormalizedLandmark>()
    for (conn in connections) {
        list.add(landmarks[conn.start()])
        if (list.size == 4) {
            return getIrisCenter(
                list[0].x, list[0].y,
                list[1].x, list[1].y,
                list[2].x, list[2].y,
                list[3].x, list[3].y
            )
        }
    }
    return null
}

fun getIrisCenter(
    x1: Float, y1: Float,
    x2: Float, y2: Float,
    x3: Float, y3: Float,
    x4: Float, y4: Float
): FloatPoint? {
    val a1: Double = (y1.toDouble() - y3) / (x1.toDouble() - x3)
    val b1: Double = y1 - (a1 * x1)
    val a2: Double = (y2.toDouble() - y4) / (x2.toDouble() - x4)
    val b2: Double = y2 - (a2 * x2)
    if (a1 == a2) return null
    val x = (b2 - b1) / (a1 - a2)
    val y = a1 * x + b1
    val radius = sqrt((x - x2).pow(2) + (y - y2).pow(2))
    val point = FloatPoint(x.toFloat(), y.toFloat(), radius.toFloat())
    point.log()
    return point
}