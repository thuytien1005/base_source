package wee.digital.sample.ui.main.fragment.home

import android.view.LayoutInflater
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.main.MainFragment

class HomeFragment : MainFragment<HomeBinding>() {

    override fun inflating(): (LayoutInflater) -> HomeBinding {
        return HomeBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }
}