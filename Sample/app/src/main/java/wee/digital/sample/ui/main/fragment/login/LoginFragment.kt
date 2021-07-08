package wee.digital.sample.ui.main.fragment.login

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.MainDirections
import wee.digital.sample.databinding.LoginBinding
import wee.digital.sample.ui.main.MainFragment

class LoginFragment : MainFragment<LoginBinding>() {

    private val vm by viewModel(LoginVM::class)

    override fun inflating(): (LayoutInflater) -> LoginBinding {
        return LoginBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.loginActionRegister)
    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.loginActionRegister -> {
                navigate(MainDirections.actionGlobalRegisterFragment())
            }
        }
    }

}