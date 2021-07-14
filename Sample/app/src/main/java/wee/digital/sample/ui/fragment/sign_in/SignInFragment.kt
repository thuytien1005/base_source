package wee.digital.sample.ui.fragment.sign_in

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.SignInBinding
import wee.digital.sample.ui.main.MainFragment

class SignInFragment : MainFragment<SignInBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> SignInBinding {
        return SignInBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewSignIn, bind.viewRegister)
        bind.inputViewEmail.text = "khang@wee.vn"
        bind.inputViewPassword.text = "123456"
    }

    override fun onLiveDataObserve() {
        userVM.firebaseUserLiveData.observe {
            it ?: return@observe
            onAuthSuccess()
        }
        signInVM.emailErrorLiveData.observe {
            setEmailError(it)
        }
        signInVM.passwordErrorLiveData.observe {
            setPasswordError(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewSignIn -> {
                signInVM.signIn(bind.inputViewEmail.trimText, bind.inputViewPassword.text)
            }
            bind.viewRegister -> {
                navigate(R.id.action_global_registerFragment)
            }
        }
    }

    /**
     * [SignInFragment] properties
     */
    private val signInVM by viewModel(SignInVM::class)

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