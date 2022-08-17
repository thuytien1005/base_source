package wee.digital.sample.ui.splash

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.darkSystemWidgets
import wee.digital.library.extension.start
import wee.digital.sample.databinding.SplashBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainActivity

class SplashActivity : BaseActivity<SplashBinding>() {

    override fun inflating(): Inflating = SplashBinding::inflate

    override fun onViewCreated() {
        darkSystemWidgets()
        hideKeyboard()
        lifecycleScope.launch {
            delay(300)
            start(MainActivity::class)
            finish()
        }
    }

    override fun onLiveDataObserve() {

    }

}