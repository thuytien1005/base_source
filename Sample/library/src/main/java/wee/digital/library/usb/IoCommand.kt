package wee.digital.library.usb

import java.io.IOException

/**
 * Cashino
 */
object IoCommand {

    const val VENDOR_ID: Int = 4070

    val hasDevice: Boolean get() = Usb.getDevice(VENDOR_ID) != null

    private var driver = UsbDriver()

    fun write(bytes: ByteArray) {
        var length = 0
        var size: Int
        val byteArray = ByteArray(4096)
        val startedTime = System.currentTimeMillis()
        while (length < bytes.size) {
            if (System.currentTimeMillis() - startedTime > 8000) break
            size = 4096
            if (length + 4096 > bytes.size) size = bytes.size - length
            System.arraycopy(bytes, length, byteArray, 0, size)
            val transferSize: Int? =
                driver.connection?.bulkTransfer(driver.endpointIn, byteArray, size, 5000)
            size = transferSize ?: throw IOException("bulk transfer error")
            if (size < 0) throw IOException("bulk transfer error")
            length += size
        }
    }

    fun read(bytes: ByteArray): Int {
        return -1
    }

    fun open() {
        driver = UsbDriver()
        val device = Usb.getDevice(VENDOR_ID) ?: return
        driver.open(device)
    }

    fun close() {
        driver.close()
    }


}


