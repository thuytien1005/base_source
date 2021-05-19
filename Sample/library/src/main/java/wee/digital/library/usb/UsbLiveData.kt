package wee.digital.library.usb

import android.hardware.usb.UsbDevice
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import wee.digital.library.util.EventLiveData

class UsbLiveData private constructor() : EventLiveData<UsbDevice>() {

    companion object {

        val instance: UsbLiveData by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            UsbLiveData()
        }
    }

    private var vendorIds: IntArray = intArrayOf()

    fun devices(vararg vendorIds: Int): UsbLiveData {
        this.vendorIds = vendorIds
        return this
    }

    fun observe(activity: AppCompatActivity, block: (UsbDevice) -> Unit) {
        for (vendorId in vendorIds) {
            val usb = Usb.getDevice(vendorId)
            when {
                null == usb -> {

                }
                Usb.hasPermission(usb) -> {
                    block(usb)
                }
                else -> {
                    Usb.requestPermission(usb)
                }
            }
            instance.value = usb
        }
        observe(activity, Observer {
            if (null != it) {
                block(it)
            }
        })
        Usb.observer(activity, *vendorIds)
    }

}