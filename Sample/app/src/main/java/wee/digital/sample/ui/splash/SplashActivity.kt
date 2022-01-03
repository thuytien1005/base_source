package wee.digital.sample.ui.splash

import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import wee.digital.library.extension.*
import wee.digital.sample.R
import wee.digital.sample.data.firebase.onFirebaseAppInit
import wee.digital.sample.databinding.SplashBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.fragment.dialog.DialogVM
import wee.digital.sample.ui.fragment.dialog.alert.AlertArg
import wee.digital.sample.ui.fragment.dialog.alert.AlertFragment
import wee.digital.sample.ui.main.MainActivity
import wee.digital.widget.extension.toastError

/**
 * Splash
 * @see <a href="">Document</a>
 * @see <a href="">Design</a>
 */
class SplashActivity : BaseActivity<SplashBinding>() {

    private val splashVM by lazyViewModel(SplashVM::class)
    private val dialogVM by lazyViewModel(DialogVM::class)
    private var launchJob: Job? = null

    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return SplashBinding::inflate
    }

    override fun activityNavController(): NavController? {
        return null
    }

    override fun onViewCreated() {
        lightSystemWidgets()
        hideKeyboard()
        onFirebaseAppInit {
            onSplash()
        }
    }

    override fun onLiveDataObserve() {
        splashVM.authSuccessLiveData.observe {
            onNavigateNext()
        }
        splashVM.networkAvailableLiveData.observe {
            onSplash()
        }
        splashVM.authErrorLiveData.observe {
            toastError(it)
        }
    }

    override fun onPause() {
        super.onPause()
        launchJob?.cancel()
    }

    /**
     *
     */
    private fun onSplash() {
        if (networkDisconnected) {
            showNetworkError()
        } else {
            dismissDialogs()
            splashVM.onAuthUser()
        }
    }

    private fun dismissDialogs() {
        dialogVM.alertLiveData.value = null
        dialogVM.webLiveData.value = null
        dialogVM.selectableLiveData.value = null
    }

    private fun showNetworkError() {
        val arg = AlertArg().apply {
            icon = R.drawable.ic_check
            title = "Không tìm thấy kết nối internet"
            message = "Vui lòng kiểm tra kết nối của thiết bị"
            cancelLabel = "Đóng"
            acceptLabel = "Cài đặt"
            acceptOnClick = { navigateWifiSettings() }
        }
        dialogVM.alertLiveData.value = arg
        AlertFragment().show(supportFragmentManager, "AlertFragment")
    }

    private fun onNavigateNext() {
        launchJob?.cancel()
        launchJob = launch {
            delay(300)
            start(MainActivity::class)
            finish()
        }
    }

}