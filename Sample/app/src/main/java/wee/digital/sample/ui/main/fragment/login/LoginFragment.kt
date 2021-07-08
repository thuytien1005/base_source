package wee.digital.sample.ui.main.fragment.login

import android.view.LayoutInflater
import android.view.View
import com.google.firebase.auth.FirebaseUser
import wee.digital.library.extension.launch
import wee.digital.library.extension.toast
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
        addClickListener(bind.viewLogin,bind.viewRegister)
    }

    override fun onLiveDataObserve() {
        firebaseVM.hasAuthLiveData.observe(this::onAuthSuccess)
        loginVM.emailErrorLiveData.observe(this::setEmailError)
        loginVM.passwordErrorLiveData.observe(this::setPasswordError)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewLogin -> {
                launch (4000){
                    toast("login")
                }
                //loginVM.onLogin(bind.inputViewEmail.trimText, bind.inputViewPassword.text)
            }
            bind.viewRegister -> {
                navigate(R.id.action_global_registerFragment)
            }
        }
    }

    fun setEmailError(it: String?) {
        bind.inputViewEmail.error = it
    }

    fun setPasswordError(it: String?) {
        bind.inputViewPassword.error = it
    }

    fun onAuthSuccess(it: FirebaseUser) {
        navigate(R.id.action_global_homeFragment) {
            setLaunchSingleTop()
        }
    }

}