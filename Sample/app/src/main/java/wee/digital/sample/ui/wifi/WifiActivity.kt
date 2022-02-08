package wee.digital.sample.ui.wifi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import wee.digital.sample.databinding.ActivityWifiBinding
import wee.digital.widget.extension.addClickListener


class WifiActivity : AppCompatActivity() {

    private val vb: ActivityWifiBinding by lazy {
        ActivityWifiBinding.inflate(layoutInflater)
    }

    private val wifiHandler = WifiHandler().also {
        it.defaultSSID = "Huy"
        it.defaultPassword = "23121990huy"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        vb.buttonOnOff.addClickListener { wifiEnable(!isWifiEnabled) }

        vb.buttonScan.addClickListener { checkPermissionAndServiceToScan() }

        vb.buttonHotspot.addClickListener { wifiHandler.turnOnHotspot(this) }

        vb.textViewList.addClickListener { wifiHandler.connect(this) }

        bindWifiState()

        wifiHandler.listen(this) { enabled ->
            runOnUiThread {
                Wifi.log("wifi enable state changed - $enabled")
                vb.buttonOnOff.text = if (enabled) "off" else "on"
                if (enabled) {
                    checkPermissionAndServiceToScan()
                } else {
                    vb.textViewList.text = "..."
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionAndServiceToScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiHandler.onDestroy(this)
    }

    /**
     *
     */
    private fun bindWifiState() {
        if (isWifiEnabled) {
            vb.textViewWifi.text = "wifi is on"
            vb.buttonOnOff.text = "off"
        } else {
            vb.textViewWifi.text = "wifi is off"
            vb.buttonOnOff.text = "on"
            vb.textViewList.text = "turn on wifi to scan"
        }
    }

    private fun checkPermissionAndServiceToScan() {
        onGrantedWifiPermission(onGranted = {
            vb.textViewList.text = null
            if (isLocationOn()) {
                startScan()
            } else {
                vb.textViewList.text = "Turn on location service to scan"
            }
        }, onDenied = {
            vb.textViewList.text = "Permission require to scan wifi"
            alertWifiPermissionDenied()
        })
    }

    private fun startScan() {
        wifiHandler.scan(this) { scanResult ->
            val sb = StringBuilder()
            scanResult.forEach {
                sb.append("SSID: ${it.SSID ?: "unknown"}\n")
                sb.append("BSSID: ${it.BSSID}\n")
                sb.append("capabilities: ${it.capabilities}\n")
                sb.append("level: ${it.level}\n")
                sb.append("\n")
            }
            vb.textViewList.text = sb.toString()
        }
    }

}