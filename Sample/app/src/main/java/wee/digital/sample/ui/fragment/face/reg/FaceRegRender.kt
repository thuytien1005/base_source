package wee.digital.sample.ui.fragment.face.reg

import android.graphics.Color
import android.opengl.GLES20
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections.Connection
import wee.digital.sample.ui.fragment.face.mesh.BaseRender
import wee.digital.sample.ui.fragment.face.mesh.FloatPoint
import wee.digital.sample.ui.fragment.face.mesh.floatArrayColor
import wee.digital.sample.ui.fragment.face.mesh.getIrisCenter


class FaceRegRender : BaseRender() {

    protected val irisColor = floatArrayColor(Color.parseColor("#7A2395ED"))
    private val drawColor = floatArrayColor(Color.parseColor("#FFFFFF"))
    private val drawThickness = 3

    override fun onRender(
        normalizedLandmarkList: LandmarkProto.NormalizedLandmarkList,
        landmarks: List<NormalizedLandmark>
    ) {
        val landmarks: List<NormalizedLandmark> = normalizedLandmarkList.landmarkList

        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_FACE_OVAL)
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_LEFT_EYE)
        drawLandmarkLines(landmarks, FaceMeshConnections.FACEMESH_RIGHT_EYE)
        if (normalizedLandmarkList.landmarkCount == FaceMesh.FACEMESH_NUM_LANDMARKS_WITH_IRISES) {
            drawIris(landmarks, FaceMeshConnections.FACEMESH_LEFT_IRIS)
            drawIris(landmarks, FaceMeshConnections.FACEMESH_RIGHT_IRIS)
        }
        drawFaceScan(landmarks, FaceMeshConnections.FACEMESH_TESSELATION)
    }


    var minYLandmark: NormalizedLandmark? = null
    var maxYLandmark: NormalizedLandmark? = null

    private fun drawFaceScan(
        landmarks: List<NormalizedLandmark>,
        connections: ImmutableSet<Connection>
    ) {
        GLES20.glUniform4fv(colorHandle, 1, drawColor, 0)
        GLES20.glLineWidth(drawThickness.toFloat())
        glEnableVertexAttribArray()
        minYLandmark = landmarks.minByOrNull { it.y }?.also {
            val circle = FloatPoint(it.x, it.y, 0.005F)
            drawCircle(circle)
        }
        maxYLandmark = landmarks.maxByOrNull { it.y }?.also {
            val circle = FloatPoint(it.x, it.y, 0.005F)
            drawCircle(circle)
        }
        landmarks.minByOrNull { it.z }?.also {
            val circle = FloatPoint(it.x, it.y, 0.005F)
            drawCircle(circle)
        }
        landmarks.maxByOrNull { it.z }?.also {
            val circle = FloatPoint(it.x, it.y, 0.005F)
            drawCircle(circle)
        }
        /*for (conn in connections) {
            val start = landmarks[conn.start()]
            val end = landmarks[conn.end()]

        }*/
    }


    private fun drawIris(
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
        //read pixel
        //val buffer = ByteBuffer.allocate(4) // 4 = (1 width) * (1 height) * (4 as per RGBA)
        //GLES20.glReadPixels(x, y, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
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

