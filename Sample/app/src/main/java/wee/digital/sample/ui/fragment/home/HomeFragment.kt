package wee.digital.sample.ui.fragment.home

import android.view.LayoutInflater
import android.view.View
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.main.MainFragment

class HomeFragment : MainFragment<HomeBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> HomeBinding {
        return HomeBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener()
    }

    override fun onLiveDataObserve() {

    }

    override fun onViewClick(v: View?) {
        when (v) {

        }
    }

    /**
     * [HomeFragment] properties
     */


}