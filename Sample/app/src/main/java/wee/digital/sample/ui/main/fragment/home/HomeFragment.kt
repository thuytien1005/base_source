package wee.digital.sample.ui.main.fragment.home

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.load
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
        val byteAvatar = Base64.decode(mainVM.userInfo.face, Base64.NO_WRAP)
        val avatar = BitmapFactory.decodeByteArray(byteAvatar, 0, byteAvatar.size)
        bind.homeAvatar.load(avatar)
        bind.homeEmail.text = mainVM.userInfo.email
        bind.homePassword.text = mainVM.userInfo.password
    }

    override fun onLiveDataObserve() {
        firebaseVM.userLiveData.observe {
            if (null == it) {
                navigateLogin()
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewLogout -> {
                auth.signOut()
            }
        }
    }

    private fun navigateLogin() {
        navigate(R.id.action_global_loginFragment) {
            setLaunchSingleTop()
        }
    }

}