package wee.digital.sample.ui.splash

import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import wee.digital.library.extension.darkSystemWidgets
import wee.digital.library.extension.networkDisconnected
import wee.digital.library.extension.start
import wee.digital.sample.R
import wee.digital.sample.data.firebase.onFirebaseAppInit
import wee.digital.sample.databinding.SplashBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.fragment.dialog.DialogVM
import wee.digital.sample.ui.fragment.dialog.alert.alertNetworkError
import wee.digital.sample.ui.fragment.ogl.getCenterPoint
import wee.digital.sample.ui.fragment.ogl.getCircle
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
        return findNavController(R.id.splashContainerView)
    }

    override fun onViewCreated() {
        darkSystemWidgets()
        hideKeyboard()
        onFirebaseAppInit { onSplash() }
        //getCircle(2.0, 1.0, 2.0, 5.0, -2.0, 1.0)     // 0        3
        //getCircle(0.0, 4.0, 2.0, 4.0, 4.0, 0.0)      // 1        1
        //getCircle(0.0, 4.0, 3.0, 4.0, 3.0, 0.0)      // 1.5      2
        //getCircle(1.0, 2.0, 5.0, 2.0, 1.0, -3.0)     // 3        -0.5
        //getCircle(
        //    0.6924091577529907, 0.36965715885162354,
        //    0.6769816875457764, 0.35549429059028625,
        //    0.6618795990943909, 0.3701876103878021
        //)
        getCenterPoint(0.0, 0.0, 0.0, 1.0, 1.1, 1.1,1.0,0.0)

        getCenterPoint(0.0, 1.0, 2.0, 1.0, 2.1, 1.1,0.0,0.0)


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
        return
        if (networkDisconnected) {
            alertNetworkError()
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

    private fun onNavigateNext() {
        launchJob?.cancel()
        launchJob = launch {
            delay(300)
            start(MainActivity::class)
            finish()
        }
    }

}