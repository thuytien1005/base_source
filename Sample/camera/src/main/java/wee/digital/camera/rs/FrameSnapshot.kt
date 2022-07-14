package wee.digital.camera.rs

import com.intel.realsense.librealsense.Frame
import wee.digital.camera.face.FaceSnapshot

class FrameSnapshot {
    val colorData: FrameData
    var depthData: FrameData
    var faceSnapshot: FaceSnapshot? = null

    constructor(colorData: FrameData, depthData: FrameData) {
        this.colorData = colorData
        this.depthData = depthData
    }

    constructor(colorFrame: Frame?, depthFrame: Frame?) {
        this.colorData = FrameData(colorFrame)
        this.depthData = FrameData(depthFrame)
    }


    fun close() {
        colorData.close()
        depthData.close()
    }


}