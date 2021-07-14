package wee.digital.sample.ui.fragment.register

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.RegisterBinding
import wee.digital.sample.ui.main.MainFragment

class RegisterFragment : MainFragment<RegisterBinding>() {

    private val vm by viewModel(RegisterVM::class)

    override fun inflating(): (LayoutInflater) -> RegisterBinding {
        return RegisterBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewRegister)
        bind.inputViewPassword.text = "123"
    }

    override fun onLiveDataObserve() {
        userVM.firebaseUserLiveData.observe {
            it ?: return@observe
            onAuthSuccess()
        }
        vm.firstNameErrorLiveData.observe {
            setFirstNameError(it)
        }
        vm.emailErrorLiveData.observe {
            setEmailError(it)
        }
        vm.passwordErrorLiveData.observe {
            setPasswordError(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewRegister -> vm.createUser(
                bind.inputViewFirstName.trimText,
                bind.inputViewLastName.trimText,
                bind.inputViewEmail.text,
                bind.inputViewPassword.text
            )
        }
    }

    private fun setFirstNameError(it: String?) {
        bind.inputViewFirstName.error = it
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