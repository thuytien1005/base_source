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
    private val drawColor = floatArrayColor(Color.parseColor("#FFFFFF"))
    private val drawThickness = 3

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

        val normalizedLandmarkList: LandmarkProto.NormalizedLandmarkList =
            result.multiFaceLandmarks()
                .firstOrNull()
                ?: return

        val landmarkList: List<LandmarkProto.NormalizedLandmark> =
            normalizedLandmarkList.landmarkList

        // Viền mặt
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_FACE_OVAL)

        // 2 mắt
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_EYE)
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_EYE)

        // Mống mắt
        if (normalizedLandmarkList.landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
            drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
        }

        // Lông mày
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_EYEBROW)
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_EYEBROW)

        // Môi
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LIPS)

        // Các điểm trên khuôn mặt
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_TESSELATION)

    }

    fun release() {
        GLES20.glDeleteProgram(program)
    }

    private fun drawLandmarkLines(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        color: FloatArray = drawColor,
        thickness: Int = drawThickness
    ) {
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        GLES20.glLineWidth(thickness.toFloat())
        for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]
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


}