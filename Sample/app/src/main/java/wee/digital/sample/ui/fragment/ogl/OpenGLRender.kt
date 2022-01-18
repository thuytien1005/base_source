package wee.digital.sample.ui.fragment.ogl

import android.graphics.Color
import android.opengl.GLES20
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import wee.digital.sample.ui.fragment.mask.floatArrayColor
import wee.digital.sample.ui.fragment.mask.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class OpenGLRender : ResultGlRenderer<FaceMeshResult> {

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
    private val drawColor = floatArrayColor(Color.parseColor("#750087FF"))
    private val drawThickness = 4
    private val irisColor = floatArrayColor(Color.parseColor("#9C00ACC1"))

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
            drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
            drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            //drawIris(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
            //drawIris(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
        }
    }

    fun release() {
        GLES20.glDeleteProgram(program)
    }

    private fun drawIris(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>
    ) {
        GLES20.glUniform4fv(colorHandle, 1, irisColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        val circle = getCircle(landmarks, connections) ?: return
        for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]
            val vertex = floatArrayOf(start.x, start.y, end.x, end.y, circle.x, circle.y)
            val vertexBuffer = ByteBuffer.allocateDirect(vertex.size * 6)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 3, 3)
        }
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
        val circle = getCenterPoint(landmarks, connections) ?: return
        for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]
            drawLines(start.x, start.y, end.x, end.y)
            drawLines(start.x, start.y, circle.x, circle.y)
        }

    }

    private fun drawLines(x1: Float, y1: Float, x2: Float, y2: Float) {
        val vertex = floatArrayOf(x1, y1, x2, y2)
        val vertexBuffer = ByteBuffer.allocateDirect(vertex.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertex)
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
    }

}

