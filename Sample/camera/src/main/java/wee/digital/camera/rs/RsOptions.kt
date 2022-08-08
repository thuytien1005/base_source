package wee.digital.camera.rs

import android.hardware.usb.UsbDevice
import androidx.lifecycle.MutableLiveData
import com.intel.realsense.librealsense.*
import wee.digital.camera.log
import wee.digital.camera.util.TimeLogger

object RsOptions {

    var align: Align? = null
    var colorizer: Colorizer? = null
    var streamConfig: Config? = null

    /**
     *
     */
    val colorConfig = RsStreamConfig("color")
    val depthConfig = RsStreamConfig("depth")
    val streamConfigLiveData = MutableLiveData<String?>()

    /**
     *
     */
    fun syncDevice(device: UsbDevice) {
        log.d("Rs apply stream config for ${device.productName}:")
        when (device.productId) {
            ProductId.D415 -> {
                colorConfig.update(1280, 720, 15)
                depthConfig.update(1280, 720, 6)
            }
            /*ProductId.SR300, ProductId.SR305 -> {
                colorConfig.update(1280, 720, 30)
                depthConfig.update(640, 480, 30)
            }*/
            else -> {
                colorConfig.update(1280, 720, 30)
                depthConfig.update(640, 480, 30)
            }
        }
        updateStreamConfig(device)
        val s =
            "RGB8: ${colorConfig.streamWidth}x${colorConfig.streamHeight} - ${colorConfig.rate}\n" +
                    "Z16  : ${depthConfig.streamWidth}x${depthConfig.streamHeight} - ${depthConfig.rate}"
        streamConfigLiveData.postValue(s)
    }

    private fun updateStreamConfig(device: UsbDevice) {
        if (align == null && device.productId != ProductId.D415) {
            align = Align(StreamType.COLOR)
        }
        if (colorizer == null) {
            colorizer = Colorizer().also { it.setValue(Option.COLOR_SCHEME, 0f) }
        }
        if (streamConfig == null) {
            streamConfig = Config().also {
                it.enableStream(
                    StreamType.COLOR, 0,
                    colorConfig.streamWidth, colorConfig.streamHeight,
                    StreamFormat.RGB8, colorConfig.rate
                )
                it.enableStream(
                    StreamType.DEPTH, 0,
                    depthConfig.streamWidth, depthConfig.streamHeight,
                    StreamFormat.Z16, depthConfig.rate
                )
            }
        }
    }

    /**
     *
     */
    val waitFramesLog = TimeLogger("waitFramesLog", false)
    val getFramesLog = TimeLogger("getFramesLog", false)
    val streamFpsLiveData = MutableLiveData<String?>()

    fun logFps(queueSize: Int = 0) {
        if (streamFpsLiveData.hasObservers()) {
            val s = "Frame queue: %2d\nFrame wait: %4d ms,\nFrame filter: %4d ms".format(
                queueSize,
                waitFramesLog.duration,
                getFramesLog.duration,
            )
            streamFpsLiveData.postValue(s)
        }
    }

}