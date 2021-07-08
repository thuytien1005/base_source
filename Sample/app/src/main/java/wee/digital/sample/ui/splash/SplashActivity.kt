package wee.digital.sample.ui.splash

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wee.digital.library.extension.start
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.SplashBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.main.MainActivity

/**
 * Splash
 * @see <a href="">Document</a>
 * @see <a href="">Design</a>
 */
class SplashActivity : BaseActivity<SplashBinding>() {

    private val vm by viewModel(SplashVM::class)

    override fun inflating(): (LayoutInflater) -> SplashBinding {
        return SplashBinding::inflate
    }

    override fun navController(): NavController? {
        return null
    }

    override fun onViewCreated() {
        vm.syncAuthUser()

        flow {
            delay(3000)
            emit(true)
        }.flowOn(Dispatchers.IO).onEach {
            println("")
        }.launchIn(lifecycleScope)
    }

    override fun onLiveDataObserve() {
        vm.navigateLiveData.observe {
            start(MainActivity::class)
            finish()
        }
    }



}