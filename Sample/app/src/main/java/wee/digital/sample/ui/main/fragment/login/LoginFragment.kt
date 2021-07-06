package wee.digital.sample.ui.main.fragment.login

import android.view.LayoutInflater
import wee.digital.sample.databinding.LoginBinding
import wee.digital.sample.ui.main.MainFragment

class LoginFragment : MainFragment<LoginBinding>() {

    override fun inflating(): (LayoutInflater) -> LoginBinding {
        return LoginBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }
}