package wee.digital.sample.ui.fragment.mask

import android.graphics.Color
import android.opengl.GLES20
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import com.google.mediapipe.solutions.facemesh.FaceMeshResult
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MaskRender : ResultGlRenderer<FaceMeshResult> {

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
        val numFaces = result.multiFaceLandmarks().size
        for (i in 0 until numFaces) {
            val landmarkList: List<LandmarkProto.NormalizedLandmark> =
                result.multiFaceLandmarks()[i].landmarkList

            if (result.multiFaceLandmarks()[i].landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
                drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
                drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
            }
            continue
            // mắt
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_LEFT_EYE)
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_EYE)
            // lông mày
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_EYEBROW)
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_LEFT_EYEBROW)
            //
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_TESSELATION)
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_FACE_OVAL)
            drawLandmarks(landmarkList, FaceMeshConnections.FACEMESH_LIPS)
        }
    }

    private fun drawLandmarks(
        faceLandmarkList: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        colorArray: FloatArray = drawColor,
        thickness: Int = drawThickness
    ) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        GLES20.glLineWidth(thickness.toFloat())
        for (c in connections) {
            val start = faceLandmarkList[c.start()]
            val end = faceLandmarkList[c.end()]
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

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun floatArrayColor(color: Int): FloatArray {
        return floatArrayOf(
            Color.red(color) / 255f,
            Color.green(color) / 255f,
            Color.blue(color) / 255f,
            Color.alpha(color) / 255f
        )
    }

    fun release() {
        GLES20.glDeleteProgram(program)
    }

}