package wee.digital.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import wee.digital.sample.databinding.ActivityWifiBinding
import wee.digital.widget.extension.*


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