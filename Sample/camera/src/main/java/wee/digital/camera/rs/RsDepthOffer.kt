package wee.digital.camera.rs

import com.intel.realsense.librealsense.Frame
import wee.digital.camera.util.SingleLiveData


object RsDepthOffer {

    var isGetDepthFrame: Boolean = false
    var depthSnapshot: FrameSnapshot? = null
    val depthFrameLiveData = SingleLiveData<FrameSnapshot?>()

    fun emit(frame: Frame?) {
        frame ?: return
        depthSnapshot?.depthData = FrameData(frame)
        depthFrameLiveData.postValue(depthSnapshot)
        isGetDepthFrame = false
    }
}