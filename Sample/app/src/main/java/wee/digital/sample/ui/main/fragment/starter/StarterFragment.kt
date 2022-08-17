package wee.digital.sample.ui.main.fragment.starter

import wee.digital.sample.R
import wee.digital.sample.databinding.StarterBinding
import wee.digital.sample.shared.Pref
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainFragment

class StarterFragment : MainFragment<StarterBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): Inflating = StarterBinding::inflate

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        when {
            !Pref.hadShownIntro -> {
                mainNavigate(R.id.action_global_introFragment) {
                    clearBackStack()
                }
            }
            else -> {
                mainNavigate(R.id.action_global_homeFragment) {
                    clearBackStack()
                }
            }
        }
    }


}