package wee.digital.sample.ui.fragment.face.mesh

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
import java.nio.FloatBuffer

class FaceMeshRender : ResultGlRenderer<FaceMeshResult> {

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
    private val drawColor = floatArrayColor(Color.parseColor("#43FFFFFF"))
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

        // Hai mắt
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_LEFT_EYE)
        drawLandmarkLines(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_EYE)

        // Mống mắt
        if (normalizedLandmarkList.landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
            drawIrisCircle(landmarkList, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            drawIrisCircle(landmarkList, FaceMeshConnections.FACEMESH_LEFT_IRIS)
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

    private fun drawIrisFilled(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        outerVertexCount: Int = 16
    ) {
        if (connections.size < 4) return
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnableVertexAttribArray(positionHandle)
        val circle = getIrisCenter(landmarks, connections) ?: return
        val outer = FloatPoint(0F, 0F)

        for (i in 0..outerVertexCount) {
            val rad: Double = 2 * Math.PI / outerVertexCount * (i + 1)
            val radius = circle.radius
            val outerX2 = (circle.x + radius * kotlin.math.cos(rad)).toFloat()
            val outerY2 = (circle.y + radius * kotlin.math.sin(rad)).toFloat()
            if (i != 0) {
                drawTriangle(circle.x, circle.y, outer.x, outer.y, outerX2, outerY2)
            }
            outer.x = outerX2
            outer.y = outerY2
        }
    }

    private fun drawIrisCircle(
        landmarks: List<LandmarkProto.NormalizedLandmark>,
        connections: ImmutableSet<FaceMeshConnections.Connection>,
        outerVertexCount: Int = 16
    ){
        if (connections.size < 4) return
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
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
                drawLine(outerX, outerY, outerX2, outerY2)
            }
            outerX = outerX2
            outerY = outerY2
        }
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
            drawLine(start.x, start.y, end.x, end.y)
        }
    }

    private fun drawTriangle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        val vertices = floatArrayOf(
            x1, y1, 0F,
            x2, y2, 0F,
            x3, y3, 0F
        )
        val vertexBuffer = makeFloatBuffer(vertices)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        val indexBuffer = makeIndexBuffer()
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_BYTE, indexBuffer)
    }

    private fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float) {
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

    private fun makeIndexBuffer(): ByteBuffer {
        val indices = byteArrayOf(0, 1, 2)
        val indexBuffer = ByteBuffer.allocateDirect(indices.size)
            .put(indices)
        indexBuffer.position(0)
        return indexBuffer
    }

}