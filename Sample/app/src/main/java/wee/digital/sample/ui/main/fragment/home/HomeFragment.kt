package wee.digital.sample.ui.main.fragment.home

import android.view.LayoutInflater
import android.view.View
import wee.digital.sample.R
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment

class HomeFragment : MainFragment<HomeBinding>() {

    override fun inflating(): (LayoutInflater) -> HomeBinding {
        return HomeBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewLogout)

    }

    override fun onLiveDataObserve() {
        firebaseVM.noAuthLiveData.observe{
            onLogout()
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewLogout -> {
                auth.signOut()
            }
        }
    }

    fun onLogout(){
        navigate(R.id.action_global_loginFragment) {
            setLaunchSingleTop()
        }
    }
}