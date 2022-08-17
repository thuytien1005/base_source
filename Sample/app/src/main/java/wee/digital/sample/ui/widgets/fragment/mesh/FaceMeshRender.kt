package wee.digital.sample.ui.widgets.fragment.mesh

import android.graphics.Color
import android.opengl.GLES20
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections.Connection

class FaceMeshRender : BaseRender() {

    private val drawColor = floatArrayColor(Color.parseColor("#43FFFFFF"))
    private val drawThickness = 3

    override fun onRender(
        normalizedLandmarkList: LandmarkProto.NormalizedLandmarkList,
        landmarks: List<NormalizedLandmark>
    ) {
        // Viền mặt
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_FACE_OVAL)
        // Hai mắt
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_LEFT_EYE)
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_RIGHT_EYE)
        // Mống mắt
        if (normalizedLandmarkList.landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
            drawIrisCircle(landmarks, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
            drawIrisCircle(landmarks, FaceMeshConnections.FACEMESH_LEFT_IRIS)
        }
        // Lông mày
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_RIGHT_EYEBROW)
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_LEFT_EYEBROW)
        // Môi
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_LIPS)
        // Các điểm trên khuôn mặt
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_TESSELATION)
    }

    /**
     * Drawing
     */
    private fun drawIrisFilled(
        landmarks: List<NormalizedLandmark>,
        connections: ImmutableSet<Connection>,
        outerVertexCount: Int = 16
    ) {
        if (connections.size < 4) return
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        glEnableVertexAttribArray()
        val circle = getIrisCenter(landmarks, connections) ?: return
        drawCircle(circle, outerVertexCount)
    }

    private fun drawIrisCircle(
        landmarks: List<NormalizedLandmark>,
        connections: ImmutableSet<Connection>,
        outerVertexCount: Int = 16
    ) {
        if (connections.size < 4) return
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        glEnableVertexAttribArray()
        val circle = getIrisCenter(landmarks, connections) ?: return
        drawRing(circle, outerVertexCount)
    }

    private fun drawLandmarkLines(
        landmarks: List<NormalizedLandmark>,
        connections: ImmutableSet<Connection>
    ) {
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        glEnableVertexAttribArray()
        for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]
            drawLine(start.x, start.y, end.x, end.y)
        }
    }

}