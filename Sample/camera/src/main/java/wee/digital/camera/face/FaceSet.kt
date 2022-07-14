package wee.digital.camera.face

import com.google.mlkit.vision.face.Face

class FaceSet {

    var largeFace: Face? = null
    var smallFace: Face? = null

    constructor(faces: List<Face>) {
        if (faces.isNullOrEmpty()) return
        if (faces.size == 1) {
            largeFace = faces.first()
            return
        }
        largeFace = faces.toList().maxByOrNull { it.boundingBox.width() }!!
        val largestWidth = largeFace!!.boundingBox.width()
        faces.forEach {
            val faceWidth = it.boundingBox.width()
            if (faceWidth >= largestWidth * 0.66 && faceWidth != largestWidth) {
                smallFace = it
            }
        }
    }

}