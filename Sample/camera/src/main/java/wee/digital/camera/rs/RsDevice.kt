package wee.digital.camera.rs

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.intel.realsense.librealsense.Sensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wee.digital.camera.app
import wee.digital.camera.log
import kotlin.reflect.KClass


object RsDevice {

    val VENDOR_ID get() = 32902

    private var usbReceiver: BroadcastReceiver? = null

    private val usbManager get() = systemService(UsbManager::class) as UsbManager

    val usbLiveData: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    val usbDevice: UsbDevice?
        get() {
            return usbManager.deviceList.entries.toList()
                .find { it.value.vendorId == VENDOR_ID }?.value
        }

    val hasPermission: Boolean
        get() {
            val usb = usbDevice ?: return false
            return usbManager.hasPermission(usb)
        }

    var sensor: Sensor? = null

    private fun <T> systemService(cls: KClass<*>): T {
        return ContextCompat.getSystemService(app, cls.java) as T
    }

    private fun requestPermission(usb: UsbDevice) {
        val intent = PendingIntent.getBroadcast(app, 0, Intent(".USB_PERMISSION"), 0)
        usbManager.requestPermission(usb, intent)
    }

    fun findRsDevice(onDeviceGranted: suspend CoroutineScope.(UsbDevice) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val usb = usbDevice
            if (usb != null && usbManager.hasPermission(usb)) {
                onDeviceGranted(usb)
                return@launch
            }
            if (usbReceiver?.isOrderedBroadcast == true) {
                app.unregisterReceiver(usbReceiver)
            }
            usbReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    synchronized(this) {
                        val data = intent?.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE)
                        val usb = data as? UsbDevice ?: return
                        if (usb.vendorId != VENDOR_ID) {
                            return
                        }
                        when {
                            intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) -> {
                                usbLiveData.postValue("GRANTED")
                                log.d("Rs device permission granted")
                            }
                            intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                                usbLiveData.postValue("ATTACHED")
                                log.d("Rs device permission attached")
                            }
                            intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                                usbLiveData.postValue("DETACHED")
                                log.d("Rs device permission detached")
                            }
                        }
                        if (usbManager.hasPermission(usb)) {
                            GlobalScope.launch(Dispatchers.Main) {
                                onDeviceGranted(usb)
                            }
                            app.unregisterReceiver(this)
                        } else {
                            requestPermission(usb)
                        }
                    }
                }
            }
            app.registerReceiver(usbReceiver, IntentFilter().also {
                it.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                it.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                it.addAction(UsbManager.EXTRA_PERMISSION_GRANTED)
            })
            if (usb != null) {
                requestPermission(usb)
            }
        }
    }

}