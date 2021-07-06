package wee.digital.sample.ui.main.fragment.splash

import android.view.LayoutInflater
import wee.digital.sample.databinding.SplashBinding
import wee.digital.sample.ui.main.MainFragment

/**
 * Splash
 * @see <a href="">Document</a>
 * @see <a href="">Design</a>
 */
class SplashFragment : MainFragment<SplashBinding>() {

    override fun inflating(): (LayoutInflater) -> SplashBinding {
        return SplashBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }

}