package wee.digital.camera.rs

import androidx.lifecycle.MutableLiveData
import com.intel.realsense.librealsense.CameraInfo
import com.intel.realsense.librealsense.Device

class RsDeviceInfo(
    var deviceName: String = "",
    var serial: String = "",
    var id: String = "",
    var firmware: String = ""
) {

    companion object {
        val liveData = MutableLiveData<RsDeviceInfo?>()

        fun sync(device: Device?) {
            device ?: return
            val deviceConfig = RsDeviceInfo().also {
                it.firmware = device.getInfo(CameraInfo.FIRMWARE_VERSION)
                it.id = device.getInfo(CameraInfo.PRODUCT_ID)
                it.serial = device.getInfo(CameraInfo.SERIAL_NUMBER)
                it.deviceName = device.getInfo(CameraInfo.NAME)
            }
            liveData.postValue(deviceConfig)
        }
    }
}