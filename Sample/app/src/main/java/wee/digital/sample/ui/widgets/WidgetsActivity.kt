package wee.digital.sample.ui.widgets

import androidx.navigation.NavController
import androidx.navigation.findNavController
import wee.digital.library.extension.navigateSettings
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.app
import wee.digital.sample.databinding.WidgetsBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.Inflating

class WidgetsActivity : BaseActivity<WidgetsBinding>() {

    override fun activityNavController(): NavController? = findNavController(R.id.widgetsFragment)

    override fun inflating(): Inflating = WidgetsBinding::inflate

    override fun onViewCreated() {

    }


}
