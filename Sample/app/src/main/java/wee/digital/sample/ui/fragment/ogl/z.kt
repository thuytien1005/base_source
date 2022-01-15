package wee.digital.sample.ui.fragment.ogl

import android.graphics.Color
import android.opengl.GLES20
import com.google.common.collect.ImmutableSet
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections
import wee.digital.sample.ui.fragment.mask.floatArrayColor
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun drawCircular(){
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

fun drawLandmarks(
    program: Int,
    faceLandmarkList: List<LandmarkProto.NormalizedLandmark>,
    connections: ImmutableSet<FaceMeshConnections.Connection>
) {
    val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
    val colorHandle = GLES20.glGetUniformLocation(program, "uColor")
    val colorArray = floatArrayColor(Color.parseColor("#95CDFF"))
    GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
    GLES20.glLineWidth(3f)

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