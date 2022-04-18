package wee.digital.library.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable

open class UsbReceiver(private val vendorIdList: IntArray) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val usb = intent?.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as? UsbDevice
            ?: return
        vendorIdList.forEach {
            if (it == usb.vendorId) {
                onUsbLiveDataUpdate(usb, intent)
                return
            }
        }
    }

    private fun Intent.getPermission(): Boolean {
        return getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
    }

    private fun onUsbLiveDataUpdate(usb: UsbDevice, intent: Intent) {

        when (intent.action) {

            UsbManager.ACTION_USB_DEVICE_DETACHED -> Usb.DETACHED

            UsbManager.ACTION_USB_DEVICE_ATTACHED -> if (Usb.hasPermission(usb)) {
                Usb.GRANTED
            } else {
                Usb.requestPermission(usb)
                Usb.ATTACHED
            }

            Usb.PERMISSION -> if (intent.getPermission()) {
                Usb.GRANTED
            } else {
                Usb.DENIED
            }

            else -> null

        } ?: return

        UsbLiveData.instance.value = usb
    }

}