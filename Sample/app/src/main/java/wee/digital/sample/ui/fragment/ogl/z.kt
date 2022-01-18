package wee.digital.sample.ui.fragment.ogl

import android.opengl.GLES20
import android.util.Log
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt


data class CircleProperty(val x: Float, val y: Float, val rad: Float)

fun getCircle(
    landmarks: List<LandmarkProto.NormalizedLandmark>,
    connections: ImmutableSet<FaceMeshConnections.Connection>
): CircleProperty? {
    if (connections.size < 3) return null
    val list = mutableListOf<LandmarkProto.NormalizedLandmark>()
    for (conn in connections) {
        list.add(landmarks[conn.start()])
        if (list.size == 3) {
            return getCircle(
                list[0].x.toDouble(), list[0].y.toDouble(),
                list[1].x.toDouble(), list[1].y.toDouble(),
                list[2].x.toDouble(), list[2].y.toDouble()
            )
        }
    }
    return null
}

fun getCircle(
    x1: Double, y1: Double,
    x2: Double, y2: Double,
    x3: Double, y3: Double
): CircleProperty {
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
    val f = ((sx13 * x12 + sy13 * x12 + sx21 * x13 + sy21 * x13) / (2 * (y31 * x12 - y21 * x13)))
    val g = ((sx13 * y12 + sy13 * y12 + sx21 * y13 + sy21 * y13) / (2 * (x31 * y12 - x21 * y13)))
    val c = (-x1.pow(2.0)) - y1.pow(2.0) - 2 * g * x1 - 2 * f * y1

    // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
    // where centre is (h = -g, k = -f) and radius r
    // as r^2 = h^2 + k^2 - c
    val h = -g
    val k = -f
    val sqrOfRad = h * h + k * k - c

    // r is the radius
    val r = sqrt(sqrOfRad)

    val fmt = DecimalFormat("#.#####")
    Log.d("circle", "Circle:")
    Log.d("circle", "Points $x1, $y1, $x2, $y2, $x3, $y3")
    Log.d("circle", "Centre (${fmt.format(h)},${fmt.format(k)})")
    Log.d("circle", "Radius ${fmt.format(r)}")
    return CircleProperty(h.toFloat(), k.toFloat(), r.toFloat())
}

fun getCenterPoint(
    landmarks: List<LandmarkProto.NormalizedLandmark>,
    connections: ImmutableSet<FaceMeshConnections.Connection>
): CircleProperty? {
    if (connections.size < 4) return null
    val list = mutableListOf<LandmarkProto.NormalizedLandmark>()
    for (conn in connections) {
        list.add(landmarks[conn.start()])
        if (list.size == 4) {
            return getCenterPoint(
                list[0].x.toDouble(), list[0].y.toDouble(),
                list[1].x.toDouble(), list[1].y.toDouble(),
                list[2].x.toDouble(), list[2].y.toDouble(),
                list[3].x.toDouble(), list[3].y.toDouble()
            )
        }
    }
    return null
}

fun getCenterPoint(
    x1: Double, y1: Double,
    x2: Double, y2: Double,
    x3: Double, y3: Double,
    x4: Double, y4: Double
): CircleProperty? {
    val m1: Double = (y1 - y3) / (x1 - x3)
    val b1: Double = y1 - m1 * x1
    val m2: Double = (y2 - y4) / (x2 - x4)
    val b2: Double = y2 - m2 * x1
    if (m1 == m2) {
        return null
    }
    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1
    val fmt = DecimalFormat("#.#####")
    Log.d("circle", "Circle:")
    Log.d("circle", "Points $x1, $y1, $x2, $y2, $x3, $y3")
    Log.d("circle", "Centre (${fmt.format(x)},${fmt.format(y)})")
    Log.d("circle", "Radius 0.0")
    return CircleProperty(x.toFloat(), y.toFloat(), 0F)
}

fun drawCircular() {
    val vertexCount = 30
    val radius = 1.0f
    val center_x = 0.0f
    val center_y = 0.0f

    // Create a buffer for vertex data
    val buffer = FloatArray(vertexCount * 2) // (x,y) for each vertex

    var idx = 0

    // Đỉnh trung tâm của quạt tam giác
    buffer[idx++] = center_x
    buffer[idx++] = center_y

    // Các đỉnh ngoài của hình tròn
    val outerVertexCount = vertexCount - 1

    for (i in 0 until outerVertexCount) {
        val percent = i / (outerVertexCount - 1).toFloat()
        val rad = (percent * 2 * Math.PI).toFloat()

        //Vertex position
        val outer_x: Float = center_x + radius * kotlin.math.cos(rad)
        val outer_y: Float = center_y + radius * kotlin.math.sin(rad)
        buffer[idx++] = outer_x
        buffer[idx++] = outer_y
    }
    // Vẽ các đường viền vòng tròn (bỏ qua đỉnh trung tâm khi bắt đầu vùng đệm)
    GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 2, outerVertexCount)
    //Vẽ hình tròn dưới dạng hình đầy
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
}









