package wee.digital.sample.ui.fragment.face.reg

import android.graphics.Color
import android.opengl.GLES20
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import wee.digital.sample.ui.fragment.face.mesh.floatArrayColor
import wee.digital.sample.ui.fragment.face.mesh.getIrisCenter
import wee.digital.sample.ui.fragment.face.mesh.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


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

        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_EYE)
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_EYE)

        if (normalizedLandmarkList.landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
            drawIris(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
        }
    }

    fun release() {
        GLES20.glDeleteProgram(program)
    }

    private fun drawIris(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        outerVertexCount: Int = 16
    ) {

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
            if (i != 0) {
                drawLines(outerX, outerY, outerX2, outerY2)
            }
            outerX = outerX2
            outerY = outerY2
        }

        //read pixel
        //val buffer = ByteBuffer.allocate(4) // 4 = (1 width) * (1 height) * (4 as per RGBA)
        //GLES20.glReadPixels(x, y, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
    }

    private fun drawLandmarkLines(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>
    ) {
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnableVertexAttribArray(positionHandle)
        for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]
            drawLines(start.x, start.y, end.x, end.y)
        }
    }

    private fun drawLines(x1: Float, y1: Float, x2: Float, y2: Float) {
        val vertices = floatArrayOf(
            x1, y1,
            x2, y2
        )
        val vertexBuffer = makeFloatBuffer(vertices)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
    }

    private fun makeFloatBuffer(vertices: FloatArray): FloatBuffer {
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
        return vertexBuffer
    }

}

