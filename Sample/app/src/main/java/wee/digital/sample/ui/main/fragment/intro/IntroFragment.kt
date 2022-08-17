package wee.digital.sample.ui.main.fragment.intro

import wee.digital.sample.R
import wee.digital.sample.databinding.IntroBinding
import wee.digital.sample.shared.Pref
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainFragment

class IntroFragment : MainFragment<IntroBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): Inflating = IntroBinding::inflate

    override fun onViewCreated() {
        vb.viewNext.addClickListener {
            Pref.hadShownIntro = true
            mainNavigate(R.id.action_global_homeFragment) {
                clearBackStack()
            }
        }
    }

    override fun onLiveDataObserve() {

    }

    /**
     * [IntroFragment] properties
     */

}