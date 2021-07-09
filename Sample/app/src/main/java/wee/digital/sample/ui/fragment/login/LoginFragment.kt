package wee.digital.sample.ui.fragment.login

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.LoginBinding
import wee.digital.sample.ui.main.MainFragment

class LoginFragment : MainFragment<LoginBinding>() {

    private val loginVM by viewModel(LoginVM::class)

    override fun inflating(): (LayoutInflater) -> LoginBinding {
        return LoginBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewLogin, bind.viewRegister, bind.loginInfo)
        bind.inputViewEmail.text = "quochuy@wee.vn"
        bind.inputViewPassword.text = "concacv1p"
    }

    override fun onLiveDataObserve() {
        firebaseVM.userLiveData.observe {
            if (null != it) {
                onAuthSuccess()
            }
        }
        loginVM.emailErrorLiveData.observe {
            setEmailError(it)
        }
        loginVM.passwordErrorLiveData.observe {
            setPasswordError(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewLogin -> {
                loginVM.onLogin(bind.inputViewEmail.trimText, bind.inputViewPassword.text)
            }
            bind.viewRegister -> {
                navigate(R.id.action_global_registerFragment)
            }
            bind.loginInfo -> {
                navigate(R.id.action_global_contactFragment)
            }
        }
    }

    private fun setEmailError(it: String?) {
        bind.inputViewEmail.error = it
    }

    private fun setPasswordError(it: String?) {
        bind.inputViewPassword.error = it
    }

    private fun onAuthSuccess() {
        navigate(R.id.action_global_homeFragment) {
            setLaunchSingleTop()
        }
    }

}