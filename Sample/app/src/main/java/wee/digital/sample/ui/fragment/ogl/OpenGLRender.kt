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
import kotlin.math.pow

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
    private val drawColor = floatArrayColor(Color.parseColor("#95CDFF"))
    private val drawThickness = 4

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
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            //drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
        }
    }

    fun release() {
        GLES20.glDeleteProgram(program)
    }

    private fun drawLandmarks(
        faceLandmarkList: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        colorArray: FloatArray = drawColor,
        thickness: Int = drawThickness
    ) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        GLES20.glLineWidth(thickness.toFloat())
        val VERTICES = 180
        val coords = FloatArray(VERTICES * 3)
        val theta = 0f
        for (conn in connections) {
            val start = faceLandmarkList[conn.start()]
            val end = faceLandmarkList[conn.end()]
            val vertex = floatArrayOf(start.x, start.y, end.x, end.y)
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

    // X 2

    private fun centerCircular(
        faceLandmarkList: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>
    ) {
        for (conn in connections) {
            val start = faceLandmarkList[conn.start()]
            val end = faceLandmarkList[conn.end()]
        }
    }

    private fun straight(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        var A = 0F
        var B = 3F
        var C = 1F

        val isTrueA = x1.pow(2) + y1.pow(2) - (2*A*x1) - (2*B*y1) + C == 0F
        val isTrueB = x2.pow(2) + y2.pow(2) - (2*A*x2) - (2*B*y2) + C == 0F
        val isTrueC = x3.pow(2) + y3.pow(2) - (2*A*x3) - (2*B*y3) + C == 0F

        val isTrueAB = B == (x1.pow(2)+y1.pow(2)-x2.pow(2)-y2.pow(2) - (2*A*x1) + (2*A*x2)) / (2*y1 - 2*y2)

        val isTrueAC = B == (x1.pow(2)-x3.pow(2)-y3.pow(2)+y1.pow(2) - (2*A*x1) + (2*A*x3)) / (2*y1 - 2*y3)

        val isTrue =    (x1.pow(2)+y1.pow(2)-x2.pow(2)-y2.pow(2) + A*(2*x2 -2*x1)) / (2*y1 - 2*y2) ==

                        (x1.pow(2)+y1.pow(2)-x3.pow(2)-y3.pow(2) + A*(2*x3 -2*x1)) / (2*y1 - 2*y3)
    }


}