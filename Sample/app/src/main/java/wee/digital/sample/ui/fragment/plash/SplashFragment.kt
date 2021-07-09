package wee.digital.sample.ui.fragment.plash

import android.graphics.Color
import android.view.LayoutInflater
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.SplashBinding
import wee.digital.sample.ui.main.MainFragment

/**
 * Splash
 * @see <a href="">Document</a>
 * @see <a href="">Design</a>
 */
class SplashFragment : MainFragment<SplashBinding>() {

    private val vm by viewModel(SplashVM::class)

    override fun inflating(): (LayoutInflater) -> SplashBinding {
        return SplashBinding::inflate
    }

    override fun onViewCreated() {
        mainActivity?.setBackground(Color.WHITE)
        vm.syncCurrentUser()
    }

    override fun onLiveDataObserve() {
        vm.nextDestinationLiveData.observe {
            navigateNext(it)
        }
    }

    private fun navigateNext(destinationId: Int) {
        navigate(destinationId) {
            setLaunchSingleTop()
        }
    }


}