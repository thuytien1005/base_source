package wee.digital.sample.ui.fragment.home

import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainFragment

class HomeFragment : MainFragment<HomeBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): Inflating = HomeBinding::inflate

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {

    }

    /**
     * [HomeFragment] properties
     */

}