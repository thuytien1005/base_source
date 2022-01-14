package wee.digital.sample.ui.fragment.home

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.main.MainFragment

class HomeFragment : MainFragment<HomeBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> ViewBinding {
        return HomeBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {

    }

    /**
     * [HomeFragment] properties
     */

}