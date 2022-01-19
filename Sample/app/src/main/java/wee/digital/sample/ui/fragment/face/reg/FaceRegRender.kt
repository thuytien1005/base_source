package wee.digital.sample.ui.fragment.face.reg

import android.graphics.Color
import android.opengl.GLES20
import android.util.Log
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import wee.digital.sample.ui.fragment.face.mesh.floatArrayColor
import wee.digital.sample.ui.fragment.face.mesh.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt


class FaceRegRender : ResultGlRenderer<FaceMeshResult> {

    private var program = 0

    private var positionHandle = 0
    private var projectionMatrixHandle = 0
    private var colorHandle = 0
    private val vertexShader = """uniform mat4 uProjectionMatrix;
        attribute vec4 vPosition;
        void main() {
          gl_Position = uProjectionMatrix * vPosition;
        }
    """
    private val fragmentShader = """precision mediump float;
        uniform vec4 uColor;
        void main() {
          gl_FragColor = uColor;
        }
    """
    private val drawColor = floatArrayColor(Color.parseColor("#FFFFFF"))
    private val drawThickness = 3

    private val irisColor = floatArrayColor(Color.parseColor("#7A2395ED"))

    override fun setupRendering() {
        program = GLES20.glCreateProgram()
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjectionMatrix")
        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
    }

    override fun renderResult(result: FaceMeshResult?, projectionMatrix: FloatArray?) {
        result ?: return
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)
        val normalizedLandmarkList: LandmarkProto.NormalizedLandmarkList =
            result.multiFaceLandmarks()
                .firstOrNull()
                ?: return
        val landmarkList: List<LandmarkProto.NormalizedLandmark> =
            normalizedLandmarkList.landmarkList
        if (normalizedLandmarkList.landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
            //drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
            //drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            //drawIris(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
            //drawIris(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            drawIris2(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
        }
    }


    fun release() {
        GLES20.glDeleteProgram(program)
    }

    var list = mutableListOf<Float>()

    //  [
    //      0.55278486, 0.5208076,
    //      0.5307918, 0.5057411,
    //      0.50829434, 0.520686,
    //      0.53058004, 0.53530246
    //  ]
    var ratio = 0f
    private fun drawIris2(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        outerVertexCount: Int = 8
    ) {
        if (ratio <= 0) return
        GLES20.glUniform4fv(colorHandle, 1, irisColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnableVertexAttribArray(positionHandle)
        val circle = FloatPoint(0.524F, 0.524F, 0.18F)
        var outerX = 0F
        var outerY = 0F
        for (i in 0..outerVertexCount) {
            val rad = 2 * Math.PI / outerVertexCount * (i + 1)
            val radius = circle.radius
            val outerX2 = (circle.x + radius * kotlin.math.cos(rad)).toFloat()
            val outerY2 = (circle.y + radius * kotlin.math.sin(rad)).toFloat()
            val vertices = floatArrayOf(
                circle.x, circle.y, 0F,
                outerX, outerY, 0F,
                outerX2, outerY2, 0F
            )
            outerX = outerX2
            outerY = outerY2
            if (i == 0) continue
            val vertexBuffer = makeFloatBuffer(vertices)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            val indexBuffer = makeIndexBuffer()
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_BYTE, indexBuffer)
        }
    }


    /**
     *
     */
    private fun drawIris(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        outerVertexCount: Int = 16
    ) {
        //read pixel
        // val buffer = ByteBuffer.allocate(4) // 4 = (1 width) * (1 height) * (4 as per RGBA)
        // GLES20.glReadPixels(x, y, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
        if (connections.size < 4) return
        GLES20.glUniform4fv(colorHandle, 1, irisColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnableVertexAttribArray(positionHandle)
        val circle = getIrisCenter(landmarks, connections) ?: return
        var outerX = 0F
        var outerY = 0F
        for (i in 0..outerVertexCount) {
            val rad: Double = 2 * Math.PI / outerVertexCount * (i + 1)
            val radius = circle.radius
            val outerX2 = (circle.x + radius * kotlin.math.cos(rad)).toFloat()
            val outerY2 = (circle.y + radius * kotlin.math.sin(rad)).toFloat()
            val vertices = floatArrayOf(
                circle.x, circle.y, 0F,
                outerX, outerY, 0F,
                outerX2, outerY2, 0F
            )
            outerX = outerX2
            outerY = outerY2
            if (i == 0) continue
            val vertexBuffer = makeFloatBuffer(vertices)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            val indexBuffer = makeIndexBuffer()
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_BYTE, indexBuffer)

        }
    }

    private fun makeFloatBuffer(arr: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(arr.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb: FloatBuffer = bb.asFloatBuffer()
        fb.put(arr)
        fb.position(0)
        return fb
    }

    private fun makeIndexBuffer(): ByteBuffer {
        val indices = byteArrayOf(0, 1, 2)
        val indexBuffer = ByteBuffer.allocateDirect(indices.size)
        indexBuffer.put(indices)
        indexBuffer.position(0)
        return indexBuffer
    }

    /**
     *
     */
    private fun drawLandmarkLines(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>
    ) {
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnableVertexAttribArray(positionHandle)
        val circle = getIrisCenter(landmarks, connections) ?: return
        for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]
            drawLines(start.x, start.y, end.x, end.y)
            drawLines(start.x, start.y, circle.x.toFloat(), circle.y.toFloat())
        }
    }

    private fun drawLines(x1: Float, y1: Float, x2: Float, y2: Float) {
        val vertex = floatArrayOf(x1, y1, x2, y2)
        val vertexBuffer = ByteBuffer.allocateDirect(vertex.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertex)
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
    }

    data class FloatPoint(val x: Float, val y: Float, val radius: Float = 0F) {

        fun log() {
            val fmt = DecimalFormat("#.#####")
            Log.d("circle", "Centre (${fmt.format(x)},${fmt.format(y)})")
            Log.d("circle", "Radius $radius")
        }
    }

    private fun getIrisCircle(
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

    private fun getIrisCircle(
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

    private fun getIrisCenter(
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

    private fun getIrisCenter(
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


}

