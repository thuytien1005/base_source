package wee.digital.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import wee.digital.sample.databinding.ActivityWifiBinding
import wee.digital.sample.utils.WifiHandler
import wee.digital.sample.utils.isWifiEnabled
import wee.digital.sample.utils.onGrantedWifiPermission
import wee.digital.sample.utils.wifiEnable
import wee.digital.widget.extension.addClickListener

// iOS:
// https://stackoverflow.com/questions/36303123/how-to-programmatically-connect-to-a-wifi-network-given-the-ssid-and-password
// https://www.youtube.com/watch?v=ssAKYGlmR4s
class WifiActivity : AppCompatActivity() {

    private val vb: ActivityWifiBinding by lazy {
        ActivityWifiBinding.inflate(layoutInflater)
    }

    private val wifiListener = WifiHandler().also {
        it.defaultSSID = "Huy"
        it.defaultPassword = "23121990huy"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        vb.buttonOnOf.text = if (isWifiEnabled) "off" else "on"
        wifiListener.listen(this, {
            vb.buttonOnOf.text = "off"
        }, {
            vb.buttonOnOf.text = "on"
        })

        vb.buttonCheck.addClickListener {
            vb.textViewCheck.text = isWifiEnabled.toString()
        }

        vb.buttonOnOf.addClickListener {
            wifiEnable(!isWifiEnabled)
        }

        vb.buttonConnect.addClickListener {
            onGrantedWifiPermission({
                wifiListener.startScan(this)
            }, {

            })
        }
    }

}